package iskallia.vault.world.data;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.GroupedSkill;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.WeekKey;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerVaultStatsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerVaultLevels";
   private final Map<UUID, PlayerVaultStats> playerMap = new HashMap<>();
   private final Map<WeekKey, List<PlayerVaultStatsData.PlayerRecordEntry>> weeklyVaultRecords = new HashMap<>();

   public PlayerVaultStats getVaultStats(Player player) {
      return this.getVaultStats(player.getUUID());
   }

   public PlayerVaultStats getVaultStats(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, PlayerVaultStats::new);
   }

   public PlayerVaultStatsData setVaultLevel(ServerPlayer player, int level) {
      this.getVaultStats(player).setVaultLevel(player.getServer(), level);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData addVaultExp(ServerPlayer player, int exp) {
      this.getVaultStats(player).addVaultExp(player.getServer(), exp);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData spendSkillPoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).spendSkillPoints(player.getServer(), amount);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData spendKnowledgePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).spendKnowledgePoints(player.getServer(), amount);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData spendArchetypePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).spendArchetypePoints(player.getServer(), amount);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData spendRegretPoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).spendRegretPoints(player.getServer(), amount);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData addSkillPoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).addSkillPoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData addExpertisePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).addExpertisePoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData refundSkillPoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).refundSkillPoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData refundKnowledgePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).refundKnowledgePoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData refundArchetypePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).refundArchetypePoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData refundRegretPoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).refundRegretPoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData addKnowledgePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).addKnowledgePoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData addArchetypePoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).addArchetypePoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData addRegretPoints(ServerPlayer player, int amount) {
      this.getVaultStats(player).addRegretPoints(amount).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetLevelAbilitiesAndExpertise(ServerPlayer player) {
      this.resetLevel(player);
      this.resetAbilities(player);
      this.resetExpertises(player);
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetAbilities(ServerPlayer player) {
      PlayerVaultStatsData statsData = get(player.getLevel());
      PlayerTalentsData talentsData = PlayerTalentsData.get(player.getLevel());
      PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(player.getLevel());
      TalentTree talentTree = talentsData.getTalents(player);
      AbilityTree abilityTree = abilitiesData.getAbilities(player);
      PlayerVaultStats stats = statsData.getVaultStats(player);
      SkillContext context = SkillContext.empty();
      talentTree.iterate(Skill.class, skill -> {
         if (!(skill instanceof GroupedSkill)) {
            while (skill instanceof LearnableSkill) {
               LearnableSkill learnable = (LearnableSkill)skill;
               if (!skill.isUnlocked()) {
                  break;
               }

               learnable.regret(context);
            }

            if (skill instanceof SpecializedSkill specialized) {
               specialized.resetSpecialization(context);
            }
         }
      });
      abilityTree.iterate(Skill.class, skill -> {
         if (!(skill instanceof GroupedSkill)) {
            while (skill instanceof LearnableSkill) {
               LearnableSkill learnable = (LearnableSkill)skill;
               if (!skill.isUnlocked()) {
                  break;
               }

               learnable.regret(context);
            }

            if (skill instanceof SpecializedSkill specialized) {
               specialized.resetSpecialization(context);
            }
         }
      });
      stats.addSkillPoints(context.getLearnPoints());
      stats.sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetKnowledge(ServerPlayer player) {
      this.getVaultStats(player).resetKnowledge(player.getLevel().getServer()).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetExpertises(ServerPlayer player) {
      this.getVaultStats(player).resetExpertise(player.getLevel().getServer()).sync(player.getLevel().getServer());
      PlayerVaultStatsData statsData = get(player.getLevel());
      PlayerExpertisesData expertisesData = PlayerExpertisesData.get(player.getLevel());
      ExpertiseTree expertisesTree = expertisesData.getExpertises(player);
      PlayerVaultStats stats = statsData.getVaultStats(player);

      for (Skill skill : expertisesTree.getAll(LearnableSkill.class, Skill::isUnlocked)) {
         while (skill.isUnlocked()) {
            SkillContext context = SkillContext.of(player);
            if (skill.getParent() instanceof GroupedSkill grouped) {
               grouped.select(skill.getId());
               skill = grouped;
            }

            if (skill instanceof LearnableSkill learnable && learnable.canRegret(context)) {
               learnable.regret(context);
               expertisesTree.sync(context);
            }
         }
      }

      stats.setExpertisePoints(0);
      stats.sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetLevel(ServerPlayer player) {
      this.getVaultStats(player).resetLevel(player.getLevel().getServer()).sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetAndReturnSkillPoints(ServerPlayer player) {
      this.getVaultStats(player).resetAndReturnSkillPoints().sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public void resetAndReturnAllPlayerExpertisePoints(ServerLevel level) {
      this.playerMap.forEach((uuid, playerVaultStatsData) -> this.getVaultStats(uuid).resetAndReturnExpertisePoints().sync(level.getServer()));
      this.setDirty();
   }

   public PlayerVaultStatsData resetAndReturnExpertisePoints(ServerPlayer player) {
      this.getVaultStats(player).resetAndReturnExpertisePoints().sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetAndReturnKnowledgePoints(ServerPlayer player) {
      this.getVaultStats(player).resetAndReturnKnowledgePoints().sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetAndReturnArchetypePoints(ServerPlayer player) {
      this.getVaultStats(player).resetAndReturnArchetypePoints().sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData resetAndReturnRegretPoints(ServerPlayer player) {
      this.getVaultStats(player).resetAndReturnRegretPoints().sync(player.getLevel().getServer());
      this.setDirty();
      return this;
   }

   public PlayerVaultStatsData reset(ServerPlayer player) {
      this.getVaultStats(player).reset(player.getServer());
      this.setDirty();
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

   public void updateFastestVaultTime(Player player, int timeTicks) {
      this.updateFastestVaultTime(new PlayerVaultStatsData.PlayerRecordEntry(player.getUUID(), player.getName().getString(), timeTicks));
   }

   private void updateFastestVaultTime(PlayerVaultStatsData.PlayerRecordEntry entry) {
      this.weeklyVaultRecords.computeIfAbsent(WeekKey.current(), key -> new ArrayList<>()).add(entry);
      this.setDirty();
   }

   public boolean isDirty() {
      return true;
   }

   public static PlayerVaultStatsData getServer() {
      return get(ServerLifecycleHooks.getCurrentServer());
   }

   public static PlayerVaultStatsData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerVaultStatsData get(MinecraftServer srv) {
      return (PlayerVaultStatsData)srv.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerVaultStatsData::create, PlayerVaultStatsData::new, "the_vault_PlayerVaultLevels");
   }

   private static PlayerVaultStatsData create(CompoundTag tag) {
      PlayerVaultStatsData data = new PlayerVaultStatsData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayerEntries", 8);
      ListTag statEntries = nbt.getList("StatEntries", 10);
      ListTag weeklyRecords = nbt.getList("WeeklyRecords", 10);
      if (playerList.size() != statEntries.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getVaultStats(playerUUID).deserializeNBT(statEntries.getCompound(i));
         }

         for (int i = 0; i < weeklyRecords.size(); i++) {
            CompoundTag tag = weeklyRecords.getCompound(i);
            WeekKey key = WeekKey.deserialize(tag.getCompound("weekKey"));
            List<PlayerVaultStatsData.PlayerRecordEntry> recordEntries = new ArrayList<>();
            ListTag entries = tag.getList("entries", 10);

            for (int j = 0; j < entries.size(); j++) {
               recordEntries.add(PlayerVaultStatsData.PlayerRecordEntry.deserialize(entries.getCompound(j)));
            }

            this.weeklyVaultRecords.put(key, recordEntries);
         }

         if (nbt.contains("RecordEntries", 9)) {
            ListTag recordList = nbt.getList("RecordEntries", 10);

            for (int i = 0; i < recordList.size(); i++) {
               this.updateFastestVaultTime(PlayerVaultStatsData.PlayerRecordEntry.deserialize(recordList.getCompound(i)));
            }
         }
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag statsList = new ListTag();
      ListTag recordWeekList = new ListTag();
      this.playerMap.forEach((uuid, stats) -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         statsList.add(stats.serializeNBT());
      });
      nbt.put("PlayerEntries", playerList);
      nbt.put("StatEntries", statsList);
      this.weeklyVaultRecords.forEach((weekKey, entries) -> {
         CompoundTag tag = new CompoundTag();
         tag.put("weekKey", weekKey.serialize());
         ListTag recordEntries = new ListTag();
         entries.forEach(entry -> recordEntries.add(entry.serialize()));
         tag.put("entries", recordEntries);
         recordWeekList.add(tag);
      });
      nbt.put("WeeklyRecords", recordWeekList);
      return nbt;
   }

   public static class PlayerRecordEntry {
      private static final PlayerVaultStatsData.PlayerRecordEntry DEFAULT = new PlayerVaultStatsData.PlayerRecordEntry(Util.NIL_UUID, "", 6000);
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

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putUUID("playerUUID", this.playerUUID);
         tag.putString("playerName", this.playerName);
         tag.putInt("tickCount", this.tickCount);
         return tag;
      }

      public static PlayerVaultStatsData.PlayerRecordEntry deserialize(CompoundTag tag) {
         return new PlayerVaultStatsData.PlayerRecordEntry(tag.getUUID("playerUUID"), tag.getString("playerName"), tag.getInt("tickCount"));
      }
   }
}
