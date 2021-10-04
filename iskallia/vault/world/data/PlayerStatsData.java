package iskallia.vault.world.data;

import iskallia.vault.altar.RequiredItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.stats.CrystalStat;
import iskallia.vault.world.stats.RaffleStat;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerStatsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerStats";
   protected VMapNBT<UUID, PlayerStatsData.Stats> playerStats = VMapNBT.ofUUID(PlayerStatsData.Stats::new);

   public PlayerStatsData() {
      this("the_vault_PlayerStats");
   }

   public PlayerStatsData(String name) {
      super(name);
   }

   public PlayerStatsData.Stats get(PlayerEntity player) {
      return this.get(player.func_110124_au());
   }

   public PlayerStatsData.Stats get(UUID playerId) {
      return this.playerStats.computeIfAbsent(playerId, uuid -> new PlayerStatsData.Stats());
   }

   public void onVaultFinished(UUID playerId, VaultRaid vault) {
      this.get(playerId).vaults.add(vault);
      this.func_76185_a();
   }

   public void onCrystalCrafted(UUID playerId, List<RequiredItem> recipe, CrystalData.Type type) {
      this.get(playerId).crystals.add(new CrystalStat(recipe, type));
      this.func_76185_a();
   }

   public void onRaffleCompleted(UUID playerId, WeightedList<String> contributors, String winner) {
      this.get(playerId).raffles.add(new RaffleStat(contributors, winner));
      this.func_76185_a();
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.playerStats.deserializeNBT(nbt.func_150295_c("Stats", 10));
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      nbt.func_218657_a("Stats", this.playerStats.serializeNBT());
      return nbt;
   }

   public static PlayerStatsData get(ServerWorld world) {
      return (PlayerStatsData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(PlayerStatsData::new, "the_vault_PlayerStats");
   }

   public static class Stats implements INBTSerializable<CompoundNBT> {
      protected VListNBT<VaultRaid, CompoundNBT> vaults = VListNBT.of(VaultRaid::new);
      protected VListNBT<CrystalStat, CompoundNBT> crystals = VListNBT.of(CrystalStat::new);
      protected VListNBT<RaffleStat, CompoundNBT> raffles = VListNBT.of(RaffleStat::new);

      public List<VaultRaid> getVaults() {
         return Collections.unmodifiableList(this.vaults);
      }

      public List<CrystalStat> getCrystals() {
         return Collections.unmodifiableList(this.crystals);
      }

      public List<RaffleStat> getRaffles() {
         return Collections.unmodifiableList(this.raffles);
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_218657_a("Vaults", this.vaults.serializeNBT());
         nbt.func_218657_a("Crystals", this.crystals.serializeNBT());
         nbt.func_218657_a("Raffles", this.raffles.serializeNBT());
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.vaults.deserializeNBT(nbt.func_150295_c("Vaults", 10));
         this.crystals.deserializeNBT(nbt.func_150295_c("Crystals", 10));
         this.raffles.deserializeNBT(nbt.func_150295_c("Raffles", 10));
      }
   }
}
