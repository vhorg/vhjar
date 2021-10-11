package iskallia.vault.world.data;

import iskallia.vault.block.item.TrophyStatueBlockItem;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.generated.ChallengeCrystalArchive;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

public class PlayerVaultStatsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerVaultLevels";
   private final Map<UUID, PlayerVaultStats> playerMap = new HashMap<>();
   private final Map<WeekKey, List<PlayerVaultStatsData.PlayerRecordEntry>> weeklyVaultRecords = new HashMap<>();
   private final Set<WeekKey> grantedRewards = new HashSet<>();

   public PlayerVaultStatsData() {
      super("the_vault_PlayerVaultLevels");
   }

   public PlayerVaultStatsData(String name) {
      super(name);
   }

   public PlayerVaultStats getVaultStats(PlayerEntity player) {
      return this.getVaultStats(player.func_110124_au());
   }

   public PlayerVaultStats getVaultStats(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, PlayerVaultStats::new);
   }

   public PlayerVaultStatsData setVaultLevel(ServerPlayerEntity player, int level) {
      this.getVaultStats(player).setVaultLevel(player.func_184102_h(), level);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData addVaultExp(ServerPlayerEntity player, int exp) {
      this.getVaultStats(player).addVaultExp(player.func_184102_h(), exp);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData spendSkillPts(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).spendSkillPoints(player.func_184102_h(), amount);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData spendKnowledgePts(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).spendKnowledgePoints(player.func_184102_h(), amount);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData addSkillPoint(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).addSkillPoints(amount).sync(player.func_71121_q().func_73046_m());
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData addKnowledgePoints(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).addKnowledgePoints(amount).sync(player.func_71121_q().func_73046_m());
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData reset(ServerPlayerEntity player) {
      this.getVaultStats(player).reset(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   @Nonnull
   public PlayerVaultStatsData.PlayerRecordEntry getFastestVaultTime() {
      return this.getFastestVaultTime(WeekKey.current());
   }

   @Nonnull
   public PlayerVaultStatsData.PlayerRecordEntry getFastestVaultTime(WeekKey week) {
      return this.weeklyVaultRecords
         .computeIfAbsent(week, key -> new ArrayList<>())
         .stream()
         .min(Comparator.comparing(PlayerVaultStatsData.PlayerRecordEntry::getTickCount))
         .orElse(PlayerVaultStatsData.PlayerRecordEntry.DEFAULT);
   }

   public void updateFastestVaultTime(PlayerEntity player, int timeTicks) {
      this.updateFastestVaultTime(new PlayerVaultStatsData.PlayerRecordEntry(player.func_110124_au(), player.func_200200_C_().getString(), timeTicks));
   }

   private void updateFastestVaultTime(PlayerVaultStatsData.PlayerRecordEntry entry) {
      this.weeklyVaultRecords.computeIfAbsent(WeekKey.current(), key -> new ArrayList<>()).add(entry);
      this.func_76185_a();
   }

   public boolean setRewardGranted(WeekKey weekKey) {
      if (this.grantedRewards.add(weekKey)) {
         this.func_76185_a();
         return true;
      } else {
         return false;
      }
   }

   public boolean hasGeneratedReward(WeekKey weekKey) {
      return this.grantedRewards.contains(weekKey);
   }

   public void generateRecordRewards(ServerWorld overWorld) {
      WeekKey week = WeekKey.previous();
      if (!this.hasGeneratedReward(week)) {
         PlayerVaultStatsData.PlayerRecordEntry previousRecord = this.getFastestVaultTime(week);
         if (previousRecord != PlayerVaultStatsData.PlayerRecordEntry.DEFAULT) {
            PlayerVaultStats stats = this.getVaultStats(previousRecord.getPlayerUUID());
            int vLevel = stats.getVaultLevel();
            NonNullList<ItemStack> loot = generateTrophyBox(overWorld, vLevel);
            loot.set(4, TrophyStatueBlockItem.getTrophy(overWorld, week));
            loot.set(13, new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
            loot.set(22, ChallengeCrystalArchive.getRandom());
            ItemStack box = new ItemStack(Items.field_221972_gr);
            box.func_196082_o().func_218657_a("BlockEntityTag", new CompoundNBT());
            ItemStackHelper.func_191282_a(box.func_196082_o().func_74775_l("BlockEntityTag"), loot);
            ScheduledItemDropData.get(overWorld).addDrop(previousRecord.getPlayerUUID(), box);
            this.grantedRewards.add(week);
            this.func_76185_a();
         }
      }
   }

   private static NonNullList<ItemStack> generateTrophyBox(ServerWorld overWorld, int vaultLevel) {
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(vaultLevel);
      LootTable bossBonusTbl = overWorld.func_73046_m().func_200249_aQ().func_186521_a(config.getScavengerCrate());
      NonNullList<ItemStack> recordLoot = NonNullList.func_191196_a();
      Builder builder = new Builder(overWorld).func_216023_a(overWorld.field_73012_v);

      while (recordLoot.size() < 27) {
         recordLoot.addAll(bossBonusTbl.func_216113_a(builder.func_216022_a(LootParameterSets.field_216260_a)));
      }

      Collections.shuffle(recordLoot);

      while (recordLoot.size() > 27) {
         recordLoot.remove(recordLoot.size() - 1);
      }

      return recordLoot;
   }

   public static void onStartup(FMLServerStartedEvent event) {
      get(event.getServer()).generateRecordRewards(event.getServer().func_241755_D_());
   }

   public static PlayerVaultStatsData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static PlayerVaultStatsData get(MinecraftServer srv) {
      return (PlayerVaultStatsData)srv.func_241755_D_().func_217481_x().func_215752_a(PlayerVaultStatsData::new, "the_vault_PlayerVaultLevels");
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT statEntries = nbt.func_150295_c("StatEntries", 10);
      ListNBT weeklyRecords = nbt.func_150295_c("WeeklyRecords", 10);
      ListNBT weeklyGenerated = nbt.func_150295_c("WeeklyRewards", 10);
      if (playerList.size() != statEntries.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getVaultStats(playerUUID).deserializeNBT(statEntries.func_150305_b(i));
         }

         for (int i = 0; i < weeklyRecords.size(); i++) {
            CompoundNBT tag = weeklyRecords.func_150305_b(i);
            WeekKey key = WeekKey.deserialize(tag.func_74775_l("weekKey"));
            List<PlayerVaultStatsData.PlayerRecordEntry> recordEntries = new ArrayList<>();
            ListNBT entries = tag.func_150295_c("entries", 10);

            for (int j = 0; j < entries.size(); j++) {
               recordEntries.add(PlayerVaultStatsData.PlayerRecordEntry.deserialize(entries.func_150305_b(j)));
            }

            this.weeklyVaultRecords.put(key, recordEntries);
         }

         for (int i = 0; i < weeklyGenerated.size(); i++) {
            WeekKey key = WeekKey.deserialize(weeklyGenerated.func_150305_b(i));
            this.grantedRewards.add(key);
         }

         if (nbt.func_150297_b("RecordEntries", 9)) {
            ListNBT recordList = nbt.func_150295_c("RecordEntries", 10);

            for (int i = 0; i < recordList.size(); i++) {
               this.updateFastestVaultTime(PlayerVaultStatsData.PlayerRecordEntry.deserialize(recordList.func_150305_b(i)));
            }
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT statsList = new ListNBT();
      ListNBT recordWeekList = new ListNBT();
      ListNBT rewardsWeekly = new ListNBT();
      this.playerMap.forEach((uuid, stats) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         statsList.add(stats.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("StatEntries", statsList);
      this.weeklyVaultRecords.forEach((weekKey, entries) -> {
         CompoundNBT tag = new CompoundNBT();
         tag.func_218657_a("weekKey", weekKey.serialize());
         ListNBT recordEntries = new ListNBT();
         entries.forEach(entry -> recordEntries.add(entry.serialize()));
         tag.func_218657_a("entries", recordEntries);
         recordWeekList.add(tag);
      });
      nbt.func_218657_a("WeeklyRecords", recordWeekList);
      this.grantedRewards.forEach(week -> rewardsWeekly.add(week.serialize()));
      nbt.func_218657_a("WeeklyRewards", rewardsWeekly);
      return nbt;
   }

   public static class PlayerRecordEntry {
      private static final PlayerVaultStatsData.PlayerRecordEntry DEFAULT = new PlayerVaultStatsData.PlayerRecordEntry(Util.field_240973_b_, "", 6000);
      private final UUID playerUUID;
      private final String playerName;
      private final int tickCount;

      public PlayerRecordEntry(UUID playerUUID, String playerName, int tickCount) {
         this.playerUUID = playerUUID;
         this.playerName = playerName;
         this.tickCount = tickCount;
      }

      public UUID getPlayerUUID() {
         return this.playerUUID;
      }

      public String getPlayerName() {
         return this.playerName;
      }

      public int getTickCount() {
         return this.tickCount;
      }

      public CompoundNBT serialize() {
         CompoundNBT tag = new CompoundNBT();
         tag.func_186854_a("playerUUID", this.playerUUID);
         tag.func_74778_a("playerName", this.playerName);
         tag.func_74768_a("tickCount", this.tickCount);
         return tag;
      }

      public static PlayerVaultStatsData.PlayerRecordEntry deserialize(CompoundNBT tag) {
         return new PlayerVaultStatsData.PlayerRecordEntry(tag.func_186857_a("playerUUID"), tag.func_74779_i("playerName"), tag.func_74762_e("tickCount"));
      }
   }
}
