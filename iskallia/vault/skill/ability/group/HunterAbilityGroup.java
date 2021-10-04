package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.config.sub.HunterChestsConfig;
import iskallia.vault.skill.ability.config.sub.HunterObjectiveConfig;
import iskallia.vault.skill.ability.config.sub.HunterSpawnerConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HunterAbilityGroup extends AbilityGroup<HunterConfig, HunterAbility<HunterConfig>> {
   private static final Color HUNTER_ENTITY_COLOR = new Color(9633792);
   private static final Color HUNTER_SPAWNER_COLOR = new Color(4653195);
   private static final Color HUNTER_CHEST_COLOR = new Color(14912768);
   private static final Color HUNTER_BLOCK_COLOR = new Color(2468864);
   @Expose
   private final List<HunterSpawnerConfig> spawnerLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterChestsConfig> chestsLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterObjectiveConfig> blocksLevelConfiguration = new ArrayList<>();

   private HunterAbilityGroup() {
      super("Hunter");
   }

   protected HunterConfig getSubConfig(String specialization, int level) {
      switch (specialization) {
         case "Hunter_Spawners":
            return this.spawnerLevelConfiguration.get(level);
         case "Hunter_Chests":
            return this.chestsLevelConfiguration.get(level);
         case "Hunter_Blocks":
            return this.blocksLevelConfiguration.get(level);
         default:
            return null;
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      switch (specialization) {
         case "Hunter_Spawners":
            return "Tracker";
         case "Hunter_Chests":
            return "Finder";
         case "Hunter_Blocks":
            return "Observer";
         default:
            return "Hunter";
      }
   }

   public static HunterAbilityGroup defaultConfig() {
      List<String> spawnerKeys = Arrays.asList("minecraft:mob_spawner", "ispawner:spawner", "ispawner:survival_spawner");
      List<String> chestKeys = Arrays.asList("minecraft:chest", "minecraft:trapped_chest", "the_vault:vault_chest_tile_entity");
      List<String> objectiveKeys = Arrays.asList(
         "the_vault:obelisk_tile_entity",
         "the_vault:scavenger_chest_tile_entity",
         "the_vault:stabilizer_tile_entity",
         "the_vault:xp_altar_tile_entity",
         "the_vault:blood_altar_tile_entity",
         "the_vault:time_altar_tile_entity",
         "the_vault:soul_altar_tile_entity",
         "the_vault:vault_treasure_chest_tile_entity"
      );
      HunterAbilityGroup group = new HunterAbilityGroup();
      group.addLevel(new HunterConfig(1, 48.0, HUNTER_ENTITY_COLOR.getRGB(), 100));
      group.spawnerLevelConfiguration.add(new HunterSpawnerConfig(1, 48.0, HUNTER_SPAWNER_COLOR.getRGB(), 100, spawnerKeys));
      group.chestsLevelConfiguration.add(new HunterChestsConfig(1, 48.0, HUNTER_CHEST_COLOR.getRGB(), 100, chestKeys));
      group.blocksLevelConfiguration.add(new HunterObjectiveConfig(1, 144.0, HUNTER_BLOCK_COLOR.getRGB(), 100, objectiveKeys));
      return group;
   }
}
