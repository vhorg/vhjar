package iskallia.vault.world.vault.logic;

import iskallia.vault.Vault;
import iskallia.vault.block.GodEyeBlock;
import iskallia.vault.block.VaultChampionTrophy;
import iskallia.vault.block.item.VaultChampionTrophyBlockItem;
import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.EffectMessage;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.FinalVaultData;
import iskallia.vault.world.data.InventorySnapshotData;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.ScheduledItemDropData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultGodEye;
import iskallia.vault.world.vault.gen.piece.VaultPortal;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.objective.CakeHuntObjective;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.player.VaultMember;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;

public class VaultLobby implements INBTSerializable<CompoundNBT>, IVaultTask {
   protected List<VaultLobby.Branch> branches = new ArrayList<>();
   protected VListNBT<UUID, StringNBT> players = VListNBT.ofUUID();
   protected Map<PlayerFavourData.VaultGodType, List<Integer>> scores = new HashMap<>();
   protected UUID bossVaultId = null;
   public InventorySnapshotData snapshots = new InventorySnapshotData("dummy") {
      @Override
      protected boolean shouldSnapshotItem(PlayerEntity player, ItemStack stack) {
         return true;
      }

      @Override
      public void createSnapshot(PlayerEntity player) {
         this.snapshotData
            .put(player.func_110124_au(), new InventorySnapshotData.Builder(player).setStackFilter(this::shouldSnapshotItem).replaceExisting().createSnapshot());
         this.func_76185_a();
      }

      @Override
      public boolean restoreSnapshot(PlayerEntity player) {
         InventorySnapshotData.InventorySnapshot snapshot = this.snapshotData.get(player.func_110124_au());
         return snapshot != null ? snapshot.apply(player) : false;
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

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      if (!this.players.contains(player.getPlayerId())) {
         this.players.add(player.getPlayerId());
      }

      player.runIfPresent(world.func_73046_m(), sPlayer -> {
         this.tickFinalBoss(vault, world, sPlayer);
         ModifiableAttributeInstance attribute = sPlayer.func_110148_a(Attributes.field_233818_a_);
         if (attribute != null) {
            attribute.func_188479_b(CakeHuntObjective.PENALTY);
         }

         if (sPlayer.func_70089_S() && this.snapshots.restoreSnapshot(sPlayer)) {
            this.snapshots.removeSnapshot(sPlayer);
         }

         Collection<VaultPortal> portals = vault.getGenerator().getPiecesAt(sPlayer.func_233580_cy_(), VaultPortal.class);
         if (!portals.isEmpty()) {
            VaultPortal portal = portals.iterator().next();
            if (this.isInPortal(world, sPlayer)) {
               String[] split = portal.getTemplate().func_110623_a().split(Pattern.quote("_"));
               PlayerFavourData.VaultGodType type = this.fromColor(split[split.length - 1]);
               if (type != null) {
                  VaultLobby.Branch branch = this.getOrCreate(portal.getUUID(), () -> new VaultLobby.Branch(portal.getUUID(), type));
                  if (branch.vaultId == null || VaultRaidData.get(world).get(branch.vaultId) == null) {
                     CrystalData data = this.createCrystalData(branch);
                     if (data != null) {
                        VaultRaid.Builder builder = data.createVault(world, null);
                        VaultRaidData.get(world).startVault(world, builder, v -> {
                           v.getProperties().create(VaultRaid.LEVEL, 300);
                           v.getProperties().create(VaultRaid.FORCE_ACTIVE, true);
                           v.getProperties().create(VaultRaid.PARENT, vault.getProperties().getValue(VaultRaid.IDENTIFIER));
                           branch.vaultId = v.getProperties().getBase(VaultRaid.IDENTIFIER).orElse(null);
                           this.initialize(branch, v);
                        });
                        sPlayer.func_242279_ag();
                     }
                  }

                  world.func_73046_m().func_222817_e(() -> {
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

   private void tickFinalBoss(VaultRaid vault, ServerWorld world, ServerPlayerEntity sPlayer) {
      if (this.bossVaultId == null) {
         List<VaultGodEye> allEyes = new ArrayList<>(vault.getGenerator().getPieces(VaultGodEye.class));
         List<VaultGodEye> filledEyes = allEyes.stream().filter(eye -> eye.isLit(world)).collect(Collectors.toList());
         if (allEyes.size() != 0 && allEyes.size() == filledEyes.size()) {
            this.bossVaultId = UUID.randomUUID();

            for (VaultLobby.Branch branch : this.branches) {
               VaultRaid v = VaultRaidData.get(world).get(branch.vaultId);
               if (v != null) {
                  for (VaultPlayer player : v.getPlayers()) {
                     VaultRaid.EXIT_SAFELY.execute(v, player, world);
                  }

                  v.getProperties().create(VaultRaid.FORCE_ACTIVE, false);
               }
            }

            for (VaultPlayer vPlayer : vault.getPlayers()) {
               vPlayer.runIfPresent(
                  world.func_73046_m(),
                  playerEntity -> {
                     FireworkRocketEntity fireworks = new FireworkRocketEntity(
                        world,
                        playerEntity.func_226277_ct_(),
                        playerEntity.func_226278_cu_(),
                        playerEntity.func_226281_cx_(),
                        new ItemStack(Items.field_196152_dE)
                     );
                     world.func_217376_c(fireworks);
                     world.func_184148_a(
                        null,
                        playerEntity.func_226277_ct_(),
                        playerEntity.func_226278_cu_(),
                        playerEntity.func_226281_cx_(),
                        SoundEvents.field_194228_if,
                        SoundCategory.MASTER,
                        1.0F,
                        1.0F
                     );
                     StringTextComponent title = new StringTextComponent("The Final Challenge");
                     title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14491166)));
                     StringTextComponent subtitle = new StringTextComponent("The Gods are watching...");
                     subtitle.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14491166)));
                     STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
                     STitlePacket subtitlePacket = new STitlePacket(Type.SUBTITLE, subtitle);
                     playerEntity.field_71135_a.func_147359_a(titlePacket);
                     playerEntity.field_71135_a.func_147359_a(subtitlePacket);
                  }
               );
            }

            ServerScheduler.INSTANCE.schedule(100, () -> this.countdown(vault, world, 15));
            ServerScheduler.INSTANCE.schedule(120, () -> this.countdown(vault, world, 14));
            ServerScheduler.INSTANCE.schedule(140, () -> this.countdown(vault, world, 13));
            ServerScheduler.INSTANCE.schedule(160, () -> this.countdown(vault, world, 12));
            ServerScheduler.INSTANCE.schedule(180, () -> this.countdown(vault, world, 11));
            ServerScheduler.INSTANCE.schedule(200, () -> this.countdown(vault, world, 10));
            ServerScheduler.INSTANCE.schedule(220, () -> this.countdown(vault, world, 9));
            ServerScheduler.INSTANCE.schedule(240, () -> this.countdown(vault, world, 8));
            ServerScheduler.INSTANCE.schedule(260, () -> this.countdown(vault, world, 7));
            ServerScheduler.INSTANCE.schedule(280, () -> this.countdown(vault, world, 6));
            ServerScheduler.INSTANCE.schedule(300, () -> this.countdown(vault, world, 5));
            ServerScheduler.INSTANCE.schedule(320, () -> this.countdown(vault, world, 4));
            ServerScheduler.INSTANCE.schedule(340, () -> this.countdown(vault, world, 3));
            ServerScheduler.INSTANCE.schedule(360, () -> this.countdown(vault, world, 2));
            ServerScheduler.INSTANCE.schedule(380, () -> this.countdown(vault, world, 1));
            ServerScheduler.INSTANCE.schedule(400, () -> {
               CrystalData data = new CrystalData();
               data.setCanGenerateTreasureRooms(false);
               data.setCanTriggerInfluences(false);
               data.setType(CrystalData.Type.FINAL_BOSS);
               data.setSelectedObjective((ResourceLocation)VaultObjective.REGISTRY.inverse().get(VaultRaid.KILL_THE_BOSS));
               VaultRaid.Builder builder = data.createVault(world, sPlayer);

               for (VaultPlayer playerx : vault.getPlayers()) {
                  playerx.runIfPresent(world.func_73046_m(), p -> this.snapshots.createSnapshot(p));
               }

               VaultRaidData.get(world).startVault(world, builder, vx -> {
                  vx.getProperties().create(VaultRaid.PARENT, vault.getProperties().getValue(VaultRaid.IDENTIFIER));
                  this.bossVaultId = vx.getProperties().getBase(VaultRaid.IDENTIFIER).orElse(null);
                  vault.getPlayers().clear();
                  vault.getProperties().create(VaultRaid.LOBBY, this);
               });
            });
         }
      }
   }

   public void countdown(VaultRaid vault, ServerWorld world, int secondsLeft) {
      EffectMessage msg = EffectMessage.playSound(ModSounds.TIMER_PANIC_TICK_SFX, SoundCategory.MASTER, 1.0F, 0.4F);

      for (VaultPlayer vPlayer : vault.getPlayers()) {
         vPlayer.runIfPresent(world.func_73046_m(), player -> {
            IFormattableTextComponent title = new StringTextComponent(String.valueOf(secondsLeft)).func_240699_a_(TextFormatting.BOLD);
            title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14491166)));
            STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
            STitlePacket subtitlePacket = new STitlePacket(Type.SUBTITLE, StringTextComponent.field_240750_d_);
            player.field_71135_a.func_147359_a(titlePacket);
            player.field_71135_a.func_147359_a(subtitlePacket);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
         });
      }
   }

   @Nullable
   public VaultLobby.Branch getBranch(UUID vaultId) {
      for (VaultLobby.Branch branch : this.branches) {
         if (branch.vaultId.equals(vaultId)) {
            return branch;
         }
      }

      return null;
   }

   private PlayerFavourData.VaultGodType fromColor(String color) {
      if ("green".equals(color)) {
         return PlayerFavourData.VaultGodType.BENEVOLENT;
      } else if ("blue".equals(color)) {
         return PlayerFavourData.VaultGodType.OMNISCIENT;
      } else if ("yellow".equals(color)) {
         return PlayerFavourData.VaultGodType.TIMEKEEPER;
      } else {
         return "red".equals(color) ? PlayerFavourData.VaultGodType.MALEVOLENCE : null;
      }
   }

   private CrystalData createCrystalData(VaultLobby.Branch branch) {
      CrystalData data = new CrystalData();
      data.setCanGenerateTreasureRooms(false);
      data.setCanTriggerInfluences(false);
      if (branch.type == PlayerFavourData.VaultGodType.BENEVOLENT) {
         data.setType(CrystalData.Type.FINAL_VELARA);
         data.setSelectedObjective((ResourceLocation)VaultObjective.REGISTRY.inverse().get(VaultRaid.CAKE_HUNT));
         data.setTargetObjectiveCount(42);
      } else if (branch.type == PlayerFavourData.VaultGodType.OMNISCIENT) {
         data.setType(CrystalData.Type.FINAL_TENOS);
         data.setSelectedObjective((ResourceLocation)VaultObjective.REGISTRY.inverse().get(VaultRaid.ARCHITECT_KILL_ALL_BOSSES));
      } else if (branch.type == PlayerFavourData.VaultGodType.TIMEKEEPER) {
         data.setType(CrystalData.Type.FINAL_WENDARR);
         data.setSelectedObjective((ResourceLocation)VaultObjective.REGISTRY.inverse().get(VaultRaid.TREASURE_HUNT));
      } else if (branch.type == PlayerFavourData.VaultGodType.MALEVOLENCE) {
         data.setType(CrystalData.Type.FINAL_IDONA);
         data.setSelectedObjective((ResourceLocation)VaultObjective.REGISTRY.inverse().get(VaultRaid.RAID_CHALLENGE));
         data.setTargetObjectiveCount(10);
      } else {
         data = null;
      }

      return data;
   }

   private void initialize(VaultLobby.Branch branch, VaultRaid vault) {
      if (branch.type == PlayerFavourData.VaultGodType.BENEVOLENT) {
         vault.getActiveObjective(CakeHuntObjective.class).ifPresent(cakeHunt -> {
            cakeHunt.setModifierChance(1.0F);
            cakeHunt.setPoolType(VaultModifiersConfig.ModifierPoolType.FINAL_VELARA_ADDS);
            cakeHunt.setHealthPenalty(2.0F);
            cakeHunt.setRoomPool(Vault.id("final_vault/velara/rooms"));
            cakeHunt.setTunnelPool(Vault.id("final_vault/velara/tunnels"));
         });
         vault.getProperties().create(VaultRaid.COW_VAULT, true);
         vault.getEvents().add(VaultRaid.REPLACE_WITH_COW);
      } else if (branch.type == PlayerFavourData.VaultGodType.OMNISCIENT) {
         vault.getActiveObjective(ArchitectSummonAndKillBossesObjective.class).ifPresent(killAll -> {
            killAll.setRoomPool(Vault.id("final_vault/tenos/rooms"));
            killAll.setTunnelPool(Vault.id("final_vault/tenos/tunnels"));
         });
      } else if (branch.type == PlayerFavourData.VaultGodType.TIMEKEEPER) {
         vault.getActiveObjective(TreasureHuntObjective.class).ifPresent(treasureHunt -> {
            treasureHunt.setSandPerModifier(this.players.size() > 1 ? ModConfigs.TREASURE_HUNT.mpSandTrigger : ModConfigs.TREASURE_HUNT.spSandTrigger);
            treasureHunt.setRoomPool(Vault.id("final_vault/wendarr/rooms"));
            treasureHunt.setTunnelPool(Vault.id("final_vault/wendarr/tunnels"));
         });
      } else if (branch.type == PlayerFavourData.VaultGodType.MALEVOLENCE) {
         vault.getActiveObjective(RaidChallengeObjective.class).ifPresent(raid -> {
            raid.setRoomPool(Vault.id("final_vault/idona/rooms"));
            raid.setTunnelPool(Vault.id("final_vault/idona/tunnels"));
         });
      }
   }

   private void joinVault(VaultRaid vault, ServerPlayerEntity player, ServerWorld world, VaultLobby.Branch branch) {
      VaultRunner runner = new VaultRunner(player.func_110124_au());
      if (branch.type == PlayerFavourData.VaultGodType.BENEVOLENT) {
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
      } else if (branch.type == PlayerFavourData.VaultGodType.OMNISCIENT) {
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
      } else if (branch.type == PlayerFavourData.VaultGodType.TIMEKEEPER) {
         runner.getBehaviours().add(new VaultBehaviour(VaultRaid.NO_OBJECTIVES_LEFT_GLOBALLY, VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.EXIT_SAFELY)));
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
         if (branch.type != PlayerFavourData.VaultGodType.MALEVOLENCE) {
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

      runner.getProperties().create(VaultRaid.LEVEL, 300);
      runner.getTimer().runTime = vault.getPlayers().stream().mapToInt(t -> t.getTimer().runTime).max().orElse(0);
      vault.getPlayers().add(runner);
      vault.getInitializer().execute(vault, runner, world);
   }

   public void exitVault(VaultRaid vault, ServerWorld world, VaultRaid parent, VaultMember member, ServerPlayerEntity player, boolean death) {
      if (vault.getProperties().getValue(VaultRaid.IDENTIFIER).equals(this.bossVaultId)) {
         if (death) {
            if (vault.getPlayers().size() == 1) {
               List<VaultGodEye> godEyes = new ArrayList<>(parent.getGenerator().getPieces(VaultGodEye.class));
               VaultGodEye target = godEyes.get(player.field_70170_p.func_201674_k().nextInt(godEyes.size()));
               BlockState state = player.func_71121_q().func_180495_p(target.getMin());
               if (state.func_235901_b_(GodEyeBlock.LIT)) {
                  player.func_71121_q().func_175656_a(target.getMin(), (BlockState)state.func_206870_a(GodEyeBlock.LIT, Boolean.FALSE));
               }

               this.bossVaultId = null;
               parent.getProperties().create(VaultRaid.LOBBY, this);
            }
         } else {
            parent.getPlayers().removeIf(vPlayer -> vPlayer.getPlayerId().equals(player.func_110124_au()));
            parent.getProperties().create(VaultRaid.FORCE_ACTIVE, false);
            int score = this.scores
               .values()
               .stream()
               .map(integers -> integers.stream().mapToInt(Integer::intValue).sum() / integers.size())
               .mapToInt(Integer::intValue)
               .sum();
            int id = FinalVaultData.get(world).getTimesCompleted(player.func_110124_au());
            FinalVaultData.get(world).onCompleted(player.func_110124_au());
            ItemStack trophy = VaultChampionTrophyBlockItem.create(
               player, VaultChampionTrophy.Variant.values()[MathHelper.func_76125_a(id, 0, VaultChampionTrophy.Variant.values().length - 1)]
            );
            VaultChampionTrophyBlockItem.setScore(trophy, score);
            ScheduledItemDropData.get(player.func_71121_q()).addDrop(player, trophy);
         }

         if (vault.getActiveObjectives().isEmpty()) {
            player.func_71121_q().func_73046_m().func_222817_e(() -> VaultRaid.EXIT_SAFELY.execute(parent, member, player.func_71121_q()));
         }
      } else if (vault.getPlayers().size() == 1 && vault.getActiveObjectives().isEmpty()) {
         Optional<VaultLobby.Branch> opt = this.branches
            .stream()
            .filter(b -> b.vaultId.equals(vault.getProperties().getValue(VaultRaid.IDENTIFIER)))
            .findFirst();
         opt.ifPresent(
            branch -> {
               Item item = null;
               PlayerFavourData.VaultGodType type = branch.getType();
               switch (type) {
                  case BENEVOLENT:
                     item = ModItems.VELARA_ESSENCE;
                     break;
                  case OMNISCIENT:
                     item = ModItems.TENOS_ESSENCE;
                     break;
                  case TIMEKEEPER:
                     item = ModItems.WENDARR_ESSENCE;
                     break;
                  case MALEVOLENCE:
                     item = ModItems.IDONA_ESSENCE;
               }

               if (item != null) {
                  ItemStack stack = new ItemStack(item, this.players.size() > 1 ? 1 : 2);
                  player.field_71071_by.func_70441_a(stack);
                  String vaultName = new String[]{"Velara's Gluttony", "Tenos' Puzzle", "Wendarr's Passage", "Idona's Wrath"}[type.ordinal()];
                  ITextComponent c0 = player.func_145748_c_().func_230532_e_().func_240699_a_(TextFormatting.LIGHT_PURPLE);
                  ITextComponent c1 = new StringTextComponent(" completed ").func_240699_a_(TextFormatting.GRAY);
                  ITextComponent c2 = new StringTextComponent(vaultName).func_240699_a_(branch.type.getChatColor());
                  ITextComponent c3 = new StringTextComponent(" and was awarded ").func_240699_a_(TextFormatting.GRAY);
                  ITextComponent c4 = new StringTextComponent(branch.type.getName() + "'s Essence").func_240699_a_(branch.type.getChatColor());
                  ITextComponent c5 = new StringTextComponent("!").func_240699_a_(TextFormatting.GRAY);
                  ITextComponent message = new StringTextComponent("")
                     .func_230529_a_(c0)
                     .func_230529_a_(c1)
                     .func_230529_a_(c2)
                     .func_230529_a_(c3)
                     .func_230529_a_(c4)
                     .func_230529_a_(c5);
                  player.func_184102_h().func_184103_al().func_232641_a_(message, ChatType.CHAT, player.func_110124_au());
                  int score = 0;
                  switch (type) {
                     case BENEVOLENT:
                     case OMNISCIENT:
                        score = vault.getPlayers()
                              .stream()
                              .filter(vPlayer -> vPlayer.getPlayerId().equals(player.func_110124_au()))
                              .findFirst()
                              .map(vPlayer -> vPlayer.getTimer().getTimeLeft())
                              .orElse(0)
                           / 20;
                        break;
                     case TIMEKEEPER:
                        score = vault.getObjective(TreasureHuntObjective.class).map(TreasureHuntObjective::getAddedSand).orElse(0);
                        break;
                     case MALEVOLENCE:
                        score = vault.getObjective(RaidChallengeObjective.class)
                           .map(RaidChallengeObjective::getDamageTaken)
                           .map(dmgTaken -> 2000 - (int)(dmgTaken / 100.0))
                           .orElse(0);
                        score = Math.max(0, score);
                  }

                  this.scores.computeIfAbsent(type, t -> new ArrayList<>()).add(score);
                  parent.getProperties().create(VaultRaid.LOBBY, this);
               }
            }
         );
      }
   }

   public boolean isInPortal(ServerWorld world, ServerPlayerEntity player) {
      AxisAlignedBB box = player.func_174813_aQ();
      BlockPos min = new BlockPos(box.field_72340_a + 0.001, box.field_72338_b + 0.001, box.field_72339_c + 0.001);
      BlockPos max = new BlockPos(box.field_72336_d - 0.001, box.field_72337_e - 0.001, box.field_72334_f - 0.001);
      Mutable pos = new Mutable();
      if (!world.func_175707_a(min, max)) {
         return false;
      } else if (player.func_242280_ah()) {
         player.func_242279_ag();
         return false;
      } else {
         for (int xx = min.func_177958_n(); xx <= max.func_177958_n(); xx++) {
            for (int yy = min.func_177956_o(); yy <= max.func_177956_o(); yy++) {
               for (int zz = min.func_177952_p(); zz <= max.func_177952_p(); zz++) {
                  BlockState state = world.func_180495_p(pos.func_181079_c(xx, yy, zz));
                  if (state.func_177230_c() == ModBlocks.VAULT_PORTAL) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT branchList = new ListNBT();
      this.branches.stream().map(VaultLobby.Branch::serializeNBT).forEach(branchList::add);
      if (this.bossVaultId != null) {
         nbt.func_74778_a("BossVaultId", this.bossVaultId.toString());
      }

      nbt.func_218657_a("Branches", branchList);
      nbt.func_218657_a("Snapshots", this.snapshots.serializeNBT());
      nbt.func_218657_a("Players", this.players.serializeNBT());
      CompoundNBT scores = new CompoundNBT();
      this.scores.forEach((type, scoreList) -> NBTHelper.writeList(scores, type.name(), scoreList, IntNBT.class, IntNBT::func_229692_a_));
      nbt.func_218657_a("scores", scores);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      if (nbt.func_150297_b("BossVaultId", 8)) {
         this.bossVaultId = UUID.fromString(nbt.func_74779_i("BossVaultId"));
      } else {
         this.bossVaultId = null;
      }

      this.players.deserializeNBT(nbt.func_150295_c("Players", 8));
      this.branches.clear();
      ListNBT branchList = nbt.func_150295_c("Branches", 10);

      for (int i = 0; i < branchList.size(); i++) {
         VaultLobby.Branch branch = new VaultLobby.Branch();
         branch.deserializeNBT(branchList.func_150305_b(i));
         this.branches.add(branch);
      }

      this.snapshots.deserializeNBT(nbt.func_74775_l("Snapshots"));
      this.scores = new HashMap<>();
      CompoundNBT scores = nbt.func_74775_l("scores");

      for (String key : scores.func_150296_c()) {
         this.scores.put(PlayerFavourData.VaultGodType.valueOf(key), NBTHelper.readList(scores, key, IntNBT.class, IntNBT::func_150287_d));
      }
   }

   public static class Branch implements INBTSerializable<CompoundNBT> {
      protected UUID portalId;
      protected UUID vaultId;
      protected PlayerFavourData.VaultGodType type;

      private Branch() {
      }

      public Branch(UUID portalId, PlayerFavourData.VaultGodType type) {
         this.portalId = portalId;
         this.type = type;
      }

      public UUID getPortalId() {
         return this.portalId;
      }

      public UUID getVaultId() {
         return this.vaultId;
      }

      public PlayerFavourData.VaultGodType getType() {
         return this.type;
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("Portal", this.portalId.toString());
         if (this.vaultId != null) {
            nbt.func_74778_a("Vault", this.vaultId.toString());
         }

         nbt.func_74778_a("Type", this.type.getName());
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.portalId = UUID.fromString(nbt.func_74779_i("Portal"));
         this.vaultId = !nbt.func_150297_b("Vault", 8) ? null : UUID.fromString(nbt.func_74779_i("Vault"));
         this.type = PlayerFavourData.VaultGodType.fromName(nbt.func_74779_i("Type"));
      }
   }
}
