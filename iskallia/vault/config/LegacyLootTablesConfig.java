package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.util.VaultRarity;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class LegacyLootTablesConfig extends Config {
   @Expose
   protected LevelEntryList<LegacyLootTablesConfig.Level> LEVELS = new LevelEntryList<>();

   @Override
   public String getName() {
      return "loot_table";
   }

   @Override
   protected void reset() {
      LegacyLootTablesConfig.Level level = new LegacyLootTablesConfig.Level(0);
      level.ALTAR = VaultMod.sId("chest/altar");
      level.COMPLETION_CRATE = new LinkedHashMap<>();
      level.CHAMPION = VaultMod.sId("entities/lvl0/champion");
      level.OFFERING_BOSS = VaultMod.sId("entities/lvl0/offering_boss");
      level.TREASURE_GOBLIN = VaultMod.sId("entities/lvl0/treasure_goblin");
      level.ARTIFACT_CHANCE = 0.01F;
      this.LEVELS.add(level);
   }

   public LootTableKey getDefaultCrate(String id, int level) {
      return VaultRegistry.LOOT_TABLE.getKey(this.getForLevel(level).getCompletionCrate(id));
   }

   @Nullable
   public LegacyLootTablesConfig.Level getForLevel(int level) {
      return this.LEVELS.getForLevel(level).orElse(null);
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      @Expose
      public int MIN_LEVEL;
      @Expose
      public String ALTAR;
      @Expose
      public Map<String, String> COMPLETION_CRATE;
      @Expose
      public String OFFERING_BOSS;
      @Expose
      public String GLADIATOR_CRATE;
      @Expose
      public String SCAVENGER_CRATE;
      @Expose
      public String CHAMPION;
      @Expose
      public String TREASURE_GOBLIN;
      @Expose
      public float ARTIFACT_CHANCE;

      public Level(int minLevel) {
         this.MIN_LEVEL = minLevel;
      }

      public ResourceLocation getChest(VaultRarity rarity) {
         return null;
      }

      public ResourceLocation getTreasureChest(VaultRarity rarity) {
         return null;
      }

      public ResourceLocation getAltarChest(VaultRarity rarity) {
         return null;
      }

      public ResourceLocation getCoopChest(VaultRarity rarity) {
         return null;
      }

      public ResourceLocation getBonusChest(VaultRarity rarity) {
         return null;
      }

      public ResourceLocation getAltar() {
         return new ResourceLocation(this.ALTAR);
      }

      public ResourceLocation getCompletionCrate(String id) {
         return new ResourceLocation(this.COMPLETION_CRATE.get(id));
      }

      public ResourceLocation getGladiatorCrate() {
         return this.GLADIATOR_CRATE == null ? null : new ResourceLocation(this.GLADIATOR_CRATE);
      }

      public ResourceLocation getScavengerCrate() {
         return new ResourceLocation(this.SCAVENGER_CRATE);
      }

      public ResourceLocation getAncientEternalBonusBox() {
         return null;
      }

      public ResourceLocation getBossBonusCrate() {
         return null;
      }

      public ResourceLocation getArenaCrate() {
         return null;
      }

      public ResourceLocation getVaultFighter() {
         return null;
      }

      public ResourceLocation getCow() {
         return null;
      }

      public ResourceLocation getTreasureGoblin() {
         return new ResourceLocation(this.TREASURE_GOBLIN);
      }

      public float getArtifactChance() {
         return this.ARTIFACT_CHANCE;
      }

      public float getSubFighterRaffleChance() {
         return 0.0F;
      }

      @Override
      public int getLevel() {
         return this.MIN_LEVEL;
      }
   }
}
