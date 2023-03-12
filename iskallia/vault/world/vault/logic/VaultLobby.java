package iskallia.vault.world.vault.logic;

import iskallia.vault.config.VaultModifierPoolsConfig;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.InventorySnapshotData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPortal;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.objective.CakeHuntObjective;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultLobby implements INBTSerializable<CompoundTag>, IVaultTask {
   protected List<VaultLobby.Branch> branches = new ArrayList<>();
   public InventorySnapshotData snapshots = new InventorySnapshotData() {
      @Override
      protected boolean shouldSnapshotItem(Player player, ItemStack stack) {
         return true;
      }

      @Override
      public void createSnapshot(Player player) {
         this.snapshotData
            .put(player.getUUID(), new InventorySnapshotData.Builder(player).setStackFilter(this::shouldSnapshotItem).replaceExisting().createSnapshot());
         this.setDirty();
      }
   };

   public VaultLobby.Branch getOrCreate(UUID portal, Supplier<VaultLobby.Branch> supplier) {
      Optional<VaultLobby.Branch> opt = this.branches.stream().filter(g -> g.portalId.equals(portal)).findFirst();
      if (!opt.isPresent()) {
         VaultLobby.Branch branch = supplier.get();
         this.branches.add(branch);
         opt = Optional.of(branch);
      }

      return opt.get();
   }

   public Optional<ServerPlayer> getServerPlayer(MinecraftServer srv, UUID playerId) {
      return Optional.ofNullable(srv.getPlayerList().getPlayer(playerId));
   }

   public void runIfPresent(MinecraftServer server, UUID playerId, Consumer<ServerPlayer> action) {
      this.getServerPlayer(server, playerId).ifPresent(action);
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      player.runIfPresent(world.getServer(), sPlayer -> {
         if (sPlayer.isAlive() && this.snapshots.restoreSnapshot(sPlayer)) {
            this.snapshots.removeSnapshot(sPlayer);
         }

         Collection<VaultPortal> portals = vault.getGenerator().getPiecesAt(sPlayer.blockPosition(), VaultPortal.class);
         if (!portals.isEmpty()) {
            VaultPortal portal = portals.iterator().next();
            if (this.isInPortal(world, sPlayer)) {
               String[] split = portal.getTemplate().getPath().split(Pattern.quote("_"));
               VaultGod type = this.fromColor(split[split.length - 1]);
               if (type != null) {
                  VaultLobby.Branch branch = this.getOrCreate(portal.getUUID(), () -> new VaultLobby.Branch(portal.getUUID(), type));
                  if (branch.vaultId == null || VaultRaidData.get(world).get(branch.vaultId) == null) {
                     CrystalData data = this.createCrystalData(branch);
                     if (data != null) {
                        VaultRaid.Builder builder = null;
                        VaultRaid newVault = VaultRaidData.get(world).startVault(world, builder, v -> {
                           v.getProperties().create(VaultRaid.LEVEL, 1000);
                           v.getProperties().create(VaultRaid.FORCE_ACTIVE, true);
                           v.getProperties().create(VaultRaid.PARENT, vault.getProperties().getValue(VaultRaid.IDENTIFIER));
                           branch.vaultId = v.getProperties().getBase(VaultRaid.IDENTIFIER).orElse(null);
                        });
                        this.initialize(branch, newVault);
                        sPlayer.setPortalCooldown();
                     }
                  }

                  world.getServer().submit(() -> {
                     if (branch.vaultId != null) {
                        VaultRaid target = VaultRaidData.get(world).get(branch.vaultId);
                        if (target != null) {
                           if (VaultRaidData.get(world).getActiveFor(player.getPlayerId()) != VaultRaidData.get(world).get(branch.vaultId)) {
                              vault.getPlayers().remove(player);
                              this.snapshots.createSnapshot(sPlayer);
                              this.joinVault(target, sPlayer, world, branch);
                           }
                        }
                     }
                  });
               }
            }
         }
      });
   }

   private VaultGod fromColor(String color) {
      if ("green".equals(color)) {
         return VaultGod.VELARA;
      } else if ("blue".equals(color)) {
         return VaultGod.TENOS;
      } else if ("yellow".equals(color)) {
         return VaultGod.WENDARR;
      } else {
         return "red".equals(color) ? VaultGod.IDONA : null;
      }
   }

   private CrystalData createCrystalData(VaultLobby.Branch branch) {
      return null;
   }

   private void initialize(VaultLobby.Branch branch, VaultRaid vault) {
      if (branch.type == VaultGod.VELARA) {
         vault.getActiveObjective(CakeHuntObjective.class).ifPresent(cakeHunt -> {
            cakeHunt.setModifierChance(1.0F);
            cakeHunt.setPoolType(VaultModifierPoolsConfig.ModifierPoolType.FINAL_VELARA_ADDS);
         });
      }
   }

   private void joinVault(VaultRaid vault, ServerPlayer player, ServerLevel world, VaultLobby.Branch branch) {
      VaultRunner runner = new VaultRunner(player.getUUID());
      if (branch.type == VaultGod.VELARA) {
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_OBJECTIVES_LEFT_GLOBALLY, VaultRaid.EXIT_SAFELY));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_TIME_LEFT, VaultRaid.EXIT_DEATH));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER.and(VaultRaid.IS_DEAD), VaultRaid.EXIT_DEATH_ALL_NO_SAVE));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_FINISHED.negate(), VaultRaid.TICK_SPAWNER.then(VaultRaid.TICK_CHEST_PITY)));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.AFTER_GRACE_PERIOD.and(VaultRaid.IS_FINISHED.negate()), VaultRaid.TICK_INFLUENCES));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER, VaultRaid.PAUSE_IN_ARENA.then(VaultRaid.CHECK_BAIL_FINAL)));
         runner.getBehaviours()
            .add(
               new VaultBehaviour(
                  VaultRaid.NO_ACTIVE_RUNNERS_LEFT,
                  VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(VaultRaid.EXIT_SAFELY)
               )
            );
         runner.getProperties().create(VaultRaid.SPAWNER, new VaultSpawner());
         runner.getTimer().start(30000);
      } else if (branch.type == VaultGod.TENOS) {
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_OBJECTIVES_LEFT_GLOBALLY, VaultRaid.EXIT_SAFELY));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_TIME_LEFT, VaultRaid.EXIT_DEATH));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER.and(VaultRaid.IS_DEAD), VaultRaid.EXIT_DEATH_ALL_NO_SAVE));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_FINISHED.negate(), VaultRaid.TICK_SPAWNER.then(VaultRaid.TICK_CHEST_PITY)));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.AFTER_GRACE_PERIOD.and(VaultRaid.IS_FINISHED.negate()), VaultRaid.TICK_INFLUENCES));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER, VaultRaid.PAUSE_IN_ARENA.then(VaultRaid.CHECK_BAIL_FINAL)));
         runner.getBehaviours()
            .add(
               new VaultBehaviour(
                  VaultRaid.NO_ACTIVE_RUNNERS_LEFT,
                  VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(VaultRaid.EXIT_SAFELY)
               )
            );
         runner.getProperties().create(VaultRaid.SPAWNER, new VaultSpawner());
         runner.getTimer().start(30000);
      } else if (branch.type == VaultGod.WENDARR) {
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_OBJECTIVES_LEFT_GLOBALLY, VaultRaid.EXIT_SAFELY));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_TIME_LEFT, VaultRaid.EXIT_DEATH));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER.and(VaultRaid.IS_DEAD), VaultRaid.EXIT_DEATH_ALL_NO_SAVE));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_FINISHED.negate(), VaultRaid.TICK_SPAWNER.then(VaultRaid.TICK_CHEST_PITY)));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.AFTER_GRACE_PERIOD.and(VaultRaid.IS_FINISHED.negate()), VaultRaid.TICK_INFLUENCES));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER, VaultRaid.PAUSE_IN_ARENA.then(VaultRaid.CHECK_BAIL_FINAL)));
         runner.getBehaviours()
            .add(
               new VaultBehaviour(
                  VaultRaid.NO_ACTIVE_RUNNERS_LEFT,
                  VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(VaultRaid.EXIT_SAFELY)
               )
            );
         runner.getProperties().create(VaultRaid.SPAWNER, new VaultSpawner());
         runner.getTimer().start(ModConfigs.TREASURE_HUNT.startTicks);
      } else {
         if (branch.type != VaultGod.IDONA) {
            return;
         }

         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_OBJECTIVES_LEFT_GLOBALLY, VaultRaid.EXIT_SAFELY));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER.and(VaultRaid.IS_DEAD), VaultRaid.EXIT_DEATH_ALL_NO_SAVE));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_FINISHED.negate(), VaultRaid.TICK_SPAWNER.then(VaultRaid.TICK_CHEST_PITY)));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.AFTER_GRACE_PERIOD.and(VaultRaid.IS_FINISHED.negate()), VaultRaid.TICK_INFLUENCES));
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.IS_RUNNER, VaultRaid.PAUSE_IN_ARENA.then(VaultRaid.CHECK_BAIL_FINAL)));
         runner.getBehaviours()
            .add(
               new VaultBehaviour(
                  VaultRaid.NO_ACTIVE_RUNNERS_LEFT,
                  VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(VaultRaid.EXIT_SAFELY)
               )
            );
         runner.getProperties().create(VaultRaid.SPAWNER, new VaultSpawner());
         runner.getProperties().create(VaultRaid.SHOW_TIMER, false);
      }

      runner.getProperties().create(VaultRaid.LEVEL, 1000);
      runner.getTimer().runTime = vault.getPlayers().stream().mapToInt(t -> t.getTimer().runTime).max().orElse(0);
      vault.getPlayers().add(runner);
      vault.getInitializer().execute(vault, runner, world);
   }

   public boolean isInPortal(ServerLevel world, ServerPlayer player) {
      AABB box = player.getBoundingBox();
      BlockPos min = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
      BlockPos max = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
      MutableBlockPos pos = new MutableBlockPos();
      if (!world.hasChunksAt(min, max)) {
         return false;
      } else if (player.isOnPortalCooldown()) {
         player.setPortalCooldown();
         return false;
      } else {
         for (int xx = min.getX(); xx <= max.getX(); xx++) {
            for (int yy = min.getY(); yy <= max.getY(); yy++) {
               for (int zz = min.getZ(); zz <= max.getZ(); zz++) {
                  BlockState state = world.getBlockState(pos.set(xx, yy, zz));
                  if (state.getBlock() == ModBlocks.VAULT_PORTAL) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag branchList = new ListTag();
      this.branches.stream().map(VaultLobby.Branch::serializeNBT).forEach(branchList::add);
      nbt.put("Branches", branchList);
      nbt.put("Snapshots", this.snapshots.save(new CompoundTag()));
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.branches.clear();
      ListTag branchList = nbt.getList("Branches", 10);

      for (int i = 0; i < branchList.size(); i++) {
         VaultLobby.Branch branch = new VaultLobby.Branch();
         branch.deserializeNBT(branchList.getCompound(i));
         this.branches.add(branch);
      }

      this.snapshots.load(nbt.getCompound("Snapshots"));
   }

   public static class Branch implements INBTSerializable<CompoundTag> {
      protected UUID portalId;
      protected UUID vaultId;
      protected VaultGod type;

      private Branch() {
      }

      public Branch(UUID portalId, VaultGod type) {
         this.portalId = portalId;
         this.type = type;
      }

      public UUID getPortalId() {
         return this.portalId;
      }

      public UUID getVaultId() {
         return this.vaultId;
      }

      public VaultGod getType() {
         return this.type;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("Portal", this.portalId.toString());
         if (this.vaultId != null) {
            nbt.putString("Vault", this.vaultId.toString());
         }

         nbt.putString("Type", this.type.getName());
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.portalId = UUID.fromString(nbt.getString("Portal"));
         this.vaultId = !nbt.contains("Vault", 8) ? null : UUID.fromString(nbt.getString("Vault"));
         this.type = VaultGod.fromName(nbt.getString("Type"));
      }
   }
}
