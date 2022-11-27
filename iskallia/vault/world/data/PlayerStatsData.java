package iskallia.vault.world.data;

import iskallia.vault.altar.RequiredItem;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.stats.CrystalStat;
import iskallia.vault.world.stats.RaffleStat;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerStatsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerStats";
   protected VMapNBT<UUID, PlayerStatsData.Stats> playerStats = VMapNBT.ofUUID(PlayerStatsData.Stats::new);

   public PlayerStatsData.Stats get(Player player) {
      return this.get(player.getUUID());
   }

   public PlayerStatsData.Stats get(UUID playerId) {
      return this.playerStats.computeIfAbsent(playerId, uuid -> new PlayerStatsData.Stats());
   }

   public void onVaultFinished(UUID playerId, VaultRaid vault) {
      this.get(playerId).vaults.add(vault);
      this.setDirty();
   }

   public void onCrystalCrafted(UUID playerId, List<RequiredItem> recipe) {
      this.get(playerId).crystals.add(new CrystalStat(recipe));
      this.setDirty();
   }

   public void onRaffleCompleted(UUID playerId, WeightedList<String> contributors, String winner) {
      this.get(playerId).raffles.add(new RaffleStat(contributors, winner));
      this.setDirty();
   }

   private static PlayerStatsData create(CompoundTag tag) {
      PlayerStatsData data = new PlayerStatsData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.playerStats.deserializeNBT(nbt.getList("Stats", 10));
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("Stats", this.playerStats.serializeNBT());
      return nbt;
   }

   public static PlayerStatsData get(MinecraftServer server) {
      return (PlayerStatsData)server.overworld().getDataStorage().computeIfAbsent(PlayerStatsData::create, PlayerStatsData::new, "the_vault_PlayerStats");
   }

   public static PlayerStatsData get() {
      return get(ServerLifecycleHooks.getCurrentServer());
   }

   public static class Stats implements INBTSerializable<CompoundTag> {
      protected VListNBT<VaultRaid, CompoundTag> vaults = VListNBT.of(VaultRaid::new);
      protected VListNBT<CrystalStat, CompoundTag> crystals = VListNBT.of(() -> new CrystalStat());
      protected VListNBT<RaffleStat, CompoundTag> raffles = VListNBT.of(RaffleStat::new);

      public List<VaultRaid> getVaults() {
         return Collections.unmodifiableList(this.vaults);
      }

      public List<CrystalStat> getCrystals() {
         return Collections.unmodifiableList(this.crystals);
      }

      public List<RaffleStat> getRaffles() {
         return Collections.unmodifiableList(this.raffles);
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.put("Vaults", this.vaults.serializeNBT());
         nbt.put("Crystals", this.crystals.serializeNBT());
         nbt.put("Raffles", this.raffles.serializeNBT());
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.vaults.deserializeNBT(nbt.getList("Vaults", 10));
         this.crystals.deserializeNBT(nbt.getList("Crystals", 10));
         this.raffles.deserializeNBT(nbt.getList("Raffles", 10));
      }
   }
}
