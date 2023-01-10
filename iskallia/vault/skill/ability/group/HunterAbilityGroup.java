package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.config.sub.HunterObjectiveConfig;
import iskallia.vault.skill.ability.config.sub.HunterTargetedConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HunterAbilityGroup extends AbilityGroup<HunterConfig, HunterAbility<HunterConfig>> {
   private static final Color HUNTER_CHEST_COLOR = new Color(14912768);
   private static final Color HUNTER_BLOCK_COLOR = new Color(2468864);
   private static final Color HUNTER_WOODEN_COLOR = new Color(12755545);
   private static final Color HUNTER_GILDED_COLOR = new Color(16776960);
   private static final Color HUNTER_LIVING_COLOR = new Color(65280);
   private static final Color HUNTER_ORNATE_COLOR = new Color(15597568);
   private static final Color HUNTER_COINS_COLOR = new Color(13464103);
   @Expose
   private final List<HunterObjectiveConfig> blocksLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterTargetedConfig> woodenLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterTargetedConfig> gildedLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterTargetedConfig> livingLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterTargetedConfig> ornateLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HunterTargetedConfig> coinsLevelConfiguration = new ArrayList<>();

   private HunterAbilityGroup() {
      super("Hunter");
   }

   protected HunterConfig getSubConfig(String specialization, int level) {
      return (HunterConfig)(switch (specialization) {
         case "Hunter_Blocks" -> (HunterObjectiveConfig)this.blocksLevelConfiguration.get(level);
         case "Hunter_Wooden" -> (HunterTargetedConfig)this.woodenLevelConfiguration.get(level);
         case "Hunter_Gilded" -> (HunterTargetedConfig)this.gildedLevelConfiguration.get(level);
         case "Hunter_Living" -> (HunterTargetedConfig)this.livingLevelConfiguration.get(level);
         case "Hunter_Ornate" -> (HunterTargetedConfig)this.ornateLevelConfiguration.get(level);
         case "Hunter_Coins" -> (HunterTargetedConfig)this.coinsLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Hunter_Blocks" -> "Observer";
         case "Hunter_Wooden" -> "Targeted (Wooden)";
         case "Hunter_Gilded" -> "Targeted (Gilded)";
         case "Hunter_Living" -> "Targeted (Living)";
         case "Hunter_Ornate" -> "Targeted (Ornate)";
         case "Hunter_Coins" -> "Targeted (Coins)";
         default -> "Hunter";
      };
   }

   public static HunterAbilityGroup defaultConfig() {
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
      List<String> targetedWooden = List.of("the_vault:wooden_chest");
      List<String> targetedGilded = List.of("the_vault:gilded_chest");
      List<String> targetedLiving = List.of("the_vault:living_chest");
      List<String> targetedOrnate = List.of("the_vault:ornate_chest");
      List<String> targetedCoins = List.of("the_vault:coin_pile");
      HunterAbilityGroup group = new HunterAbilityGroup();
      group.addLevel(new HunterConfig(1, 1, 10, 1, 10.0F, 48.0, HUNTER_CHEST_COLOR.getRGB(), 100, chestKeys));
      group.blocksLevelConfiguration.add(new HunterObjectiveConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_BLOCK_COLOR.getRGB(), 100, objectiveKeys));
      group.woodenLevelConfiguration.add(new HunterTargetedConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_WOODEN_COLOR.getRGB(), 100, targetedWooden));
      group.gildedLevelConfiguration.add(new HunterTargetedConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_GILDED_COLOR.getRGB(), 100, targetedGilded));
      group.livingLevelConfiguration.add(new HunterTargetedConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_LIVING_COLOR.getRGB(), 100, targetedLiving));
      group.ornateLevelConfiguration.add(new HunterTargetedConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_ORNATE_COLOR.getRGB(), 100, targetedOrnate));
      group.coinsLevelConfiguration.add(new HunterTargetedConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_COINS_COLOR.getRGB(), 100, targetedCoins));
      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.blocksLevelConfiguration});
   }
}
