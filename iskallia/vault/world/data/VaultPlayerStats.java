package iskallia.vault.world.data;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.network.message.VaultPlayerStatsMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class VaultPlayerStats extends SavedData {
   protected static final String DATA_NAME = "the_vault_VaultPlayerStats";
   private final VMapNBT<UUID, VaultPlayerStats.Entry> pending = VMapNBT.ofUUID(VaultPlayerStats.Entry::new);

   public static void addStats(UUID playerId, UUID vaultId) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      get(server).pending.computeIfAbsent(playerId, uuid -> new VaultPlayerStats.Entry()).vaultIds.add(vaultId);
      ServerPlayer player = server.getPlayerList().getPlayer(playerId);
      if (player != null) {
         if (!player.isDeadOrDying()) {
            prompt(player);
         }

         get(server).setDirty();
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("pending", this.pending.serializeNBT());
      return nbt;
   }

   public void load(CompoundTag nbt) {
      this.pending.deserializeNBT(nbt.getList("pending", 10));
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player && !player.isDeadOrDying()) {
         prompt((ServerPlayer)event.getPlayer());
      }
   }

   public static void prompt(ServerPlayer player) {
      for (UUID id : get(ServerLifecycleHooks.getCurrentServer()).pending.getOrDefault(player.getUUID(), new VaultPlayerStats.Entry()).vaultIds) {
         VaultSnapshot snapshot = getSnapshot(id).orElse(null);
         if (snapshot != null) {
            ModNetwork.CHANNEL.sendTo(new VaultPlayerStatsMessage.S2C(snapshot), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   public static void consume(ServerPlayer player, UUID vaultId) {
      List<UUID> ids = get(ServerLifecycleHooks.getCurrentServer()).pending.getOrDefault(player.getUUID(), new VaultPlayerStats.Entry()).vaultIds;
      if (ids.contains(vaultId)) {
         VaultSnapshot snapshot = getSnapshot(vaultId).orElse(null);
         if (snapshot != null && snapshot.getEnd().has(Vault.STATS)) {
            StatCollector stats = snapshot.getEnd().get(Vault.STATS).get(player.getUUID());
            PlayerVaultStatsData statsData = PlayerVaultStatsData.get(player.getLevel());
            int experience = stats.getExperience(snapshot.getEnd());
            statsData.addVaultExp(player, experience);
            VaultDollItem.onVaultCompletion(player, vaultId, getSnapshot(vaultId).map(vault -> vault.getEnd().get(Vault.LEVEL).get()).orElse(0), experience);
            if (stats.getCompletion() == Completion.COMPLETED) {
               PlayerSpiritRecoveryData.get(player.getLevel()).decreaseMultiplierOnCompletion(player.getUUID());
            }

            for (ItemStack stack : stats.get(StatCollector.REWARD)) {
               player.getInventory().placeItemBackInInventory(stack.copy());
            }

            ids.remove(vaultId);
            prompt(player);
            get(ServerLifecycleHooks.getCurrentServer()).setDirty();
         }
      }
   }

   public static Optional<VaultSnapshot> getSnapshot(UUID vaultId) {
      VaultSnapshot snapshot = VaultSnapshots.get(vaultId);
      Vault end = null;
      if (snapshot != null) {
         if (snapshot.getEnd() == null) {
            end = ServerVaults.get(vaultId).orElse(null);
         } else {
            end = snapshot.getEnd();
         }
      }

      return end == null ? Optional.empty() : Optional.of(new VaultSnapshot(snapshot.getVersion(), snapshot.getStart(), end));
   }

   public static VaultPlayerStats get(MinecraftServer server) {
      return (VaultPlayerStats)server.overworld()
         .getDataStorage()
         .computeIfAbsent(VaultPlayerStats::create, VaultPlayerStats::new, "the_vault_VaultPlayerStats");
   }

   private static VaultPlayerStats create(CompoundTag tag) {
      VaultPlayerStats data = new VaultPlayerStats();
      data.load(tag);
      return data;
   }

   private static class Entry implements INBTSerializable<CompoundTag> {
      public VListNBT<UUID, StringTag> vaultIds = VListNBT.ofUUID();

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.put("vaultIds", this.vaultIds.serializeNBT());
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.vaultIds.deserializeNBT(nbt.getList("vaultIds", 8));
      }
   }
}
