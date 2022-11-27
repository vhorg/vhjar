package iskallia.vault.world.data;

import iskallia.vault.attribute.RegistryKeyAttribute;
import iskallia.vault.backup.BackupManager;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultMember;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.LogicalSide;

public class VaultRaidData extends SavedData {
   protected static final String DATA_NAME = "the_vault_VaultRaid";
   private VListNBT<VaultRaid, CompoundTag> vaults = VListNBT.of(VaultRaid::new);
   private MutableBlockPos nextVaultPos = BlockPos.ZERO.mutable();
   @Deprecated
   private VMapNBT<UUID, VaultRaid> activeVaults = VMapNBT.ofUUID(VaultRaid::new);

   public VaultRaid get(UUID vaultId) {
      return this.vaults
         .stream()
         .filter(vault -> vaultId.equals(vault.getProperties().getBaseOrDefault(VaultRaid.IDENTIFIER, (UUID)null)))
         .findFirst()
         .orElse(null);
   }

   public VaultRaid getActiveFor(ServerPlayer player) {
      return this.getActiveFor(player.getUUID());
   }

   public VaultRaid getActiveFor(UUID playerId) {
      return this.vaults.stream().filter(vault -> vault.getPlayer(playerId).isPresent()).findFirst().orElse(null);
   }

   @Nullable
   public VaultRaid getAt(ServerLevel world, BlockPos pos) {
      return this.vaults
         .stream()
         .filter(vault -> world.dimension() == vault.getProperties().<ResourceKey<Level>, RegistryKeyAttribute<Level>>getValue(VaultRaid.DIMENSION))
         .filter(vault -> {
            Optional<BoundingBox> box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX);
            return box.isPresent() && box.get().isInside(pos);
         })
         .findFirst()
         .orElse(null);
   }

   public void remove(UUID vaultId) {
      this.vaults.removeIf(vault -> vault.getProperties().getValue(VaultRaid.IDENTIFIER).equals(vaultId));
   }

   public void remove(MinecraftServer server, UUID playerId) {
      VaultRaid vault = this.getActiveFor(playerId);
      if (vault != null) {
         ServerLevel world = server.getLevel(vault.getProperties().getValue(VaultRaid.DIMENSION));
         vault.getPlayer(playerId).ifPresent(player -> {
            if (!player.hasExited() && !(player instanceof VaultMember)) {
               VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(VaultRaid.EXIT_SAFELY).execute(vault, player, world);
            }
         });
         PlayerStatsData.get().onVaultFinished(playerId, vault);
      }
   }

   public VaultRaid startVault(ServerLevel world, VaultRaid.Builder builder) {
      return this.startVault(world, builder, vault -> {});
   }

   public VaultRaid startVault(ServerLevel world, VaultRaid.Builder builder, Consumer<VaultRaid> onBuild) {
      MinecraftServer server = world.getServer();
      VaultRaid vault = builder.build();
      onBuild.accept(vault);
      builder.getLevelInitializer().executeForAllPlayers(vault, world);
      Optional<ResourceKey<Level>> dimension = vault.getProperties().getBase(VaultRaid.DIMENSION);
      if (dimension.isPresent()) {
         world = server.getLevel(dimension.get());
      } else {
         vault.getProperties().create(VaultRaid.DIMENSION, world.dimension());
      }

      ServerLevel destination = dimension.isPresent() ? server.getLevel(dimension.get()) : world;
      server.submit(() -> {
         try {
            vault.getGenerator().generate(destination, vault, this.nextVaultPos);
            vault.getPlayers().forEach(player -> {
               player.runIfPresent(server, sPlayer -> BackupManager.createPlayerInventorySnapshot(sPlayer));
               this.remove(server, player.getPlayerId());
               vault.getInitializer().execute(vault, player, destination);
            });
            this.vaults.add(vault);
         } catch (Exception var5x) {
            var5x.printStackTrace();
         }
      });
      return vault;
   }

   public void tick(ServerLevel world) {
      new ArrayList<>(this.vaults)
         .stream()
         .filter(vault -> vault.getProperties().<ResourceKey<Level>, RegistryKeyAttribute<Level>>getValue(VaultRaid.DIMENSION) == world.dimension())
         .forEach(vault -> vault.tick(world));
      List<UUID> completed = new ArrayList<>();
      this.vaults.removeIf(vault -> {
         if (vault.isFinished()) {
            vault.getPlayers().forEach(player -> completed.add(player.getPlayerId()));
         }

         return vault.isFinished();
      });
      completed.forEach(uuid -> this.remove(world.getServer(), uuid));
   }

   public static void onTick(WorldTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.START) {
         get((ServerLevel)event.world).tick((ServerLevel)event.world);
      }
   }

   public boolean isDirty() {
      return true;
   }

   private static VaultRaidData create(CompoundTag tag) {
      VaultRaidData data = new VaultRaidData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      Map<UUID, VaultRaid> foundVaults = new HashMap<>();
      ListTag vaults = nbt.getList("ActiveVaults", 10);

      for (int i = 0; i < vaults.size(); i++) {
         CompoundTag tag = vaults.getCompound(i);
         UUID playerId = UUID.fromString(tag.getString("Key"));
         VaultRaid vault = new VaultRaid();
         vault.deserializeNBT(tag.getCompound("Value"));
         UUID vaultId = vault.getProperties().getBaseOrDefault(VaultRaid.IDENTIFIER, Util.NIL_UUID);
         if (foundVaults.containsKey(vaultId)) {
            vault = foundVaults.get(vaultId);
         } else {
            foundVaults.put(vaultId, vault);
         }

         this.activeVaults.put(playerId, vault);
      }

      this.vaults.deserializeNBT(nbt.getList("Vaults", 10));
      this.vaults.addAll(this.activeVaults.values());
      int[] pos = nbt.getIntArray("NextVaultPos");
      this.nextVaultPos = new MutableBlockPos(pos[0], pos[1], pos[2]);
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("Vaults", this.vaults.serializeNBT());
      nbt.putIntArray("NextVaultPos", new int[]{this.nextVaultPos.getX(), this.nextVaultPos.getY(), this.nextVaultPos.getZ()});
      return nbt;
   }

   public static VaultRaidData get(ServerLevel world) {
      return (VaultRaidData)world.getServer().overworld().getDataStorage().computeIfAbsent(VaultRaidData::create, VaultRaidData::new, "the_vault_VaultRaid");
   }
}
