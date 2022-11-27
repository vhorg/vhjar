package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.config.sub.HunterObjectiveConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HunterAbilityGroup extends AbilityGroup<HunterConfig, HunterAbility<HunterConfig>> {
   private static final Color HUNTER_CHEST_COLOR = new Color(14912768);
   private static final Color HUNTER_BLOCK_COLOR = new Color(2468864);
   @Expose
   private final List<HunterObjectiveConfig> blocksLevelConfiguration = new ArrayList<>();

   private HunterAbilityGroup() {
      super("Hunter");
   }

   protected HunterConfig getSubConfig(String specialization, int level) {
      byte var4 = -1;
      switch (specialization.hashCode()) {
         case 504493861:
            if (specialization.equals("Hunter_Blocks")) {
               var4 = 0;
            }
         default:
            switch (var4) {
               case 0:
                  return this.blocksLevelConfiguration.get(level);
               default:
                  return null;
            }
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      byte var3 = -1;
      switch (specialization.hashCode()) {
         case 504493861:
            if (specialization.equals("Hunter_Blocks")) {
               var3 = 0;
            }
         default:
            switch (var3) {
               case 0:
                  return "Observer";
               default:
                  return "Hunter";
            }
      }
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
      HunterAbilityGroup group = new HunterAbilityGroup();
      group.addLevel(new HunterConfig(1, 1, 10, 1, 10.0F, 48.0, HUNTER_CHEST_COLOR.getRGB(), 100, chestKeys));
      group.blocksLevelConfiguration.add(new HunterObjectiveConfig(1, 1, 10, 1, 10.0F, 144.0, HUNTER_BLOCK_COLOR.getRGB(), 100, objectiveKeys));
      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.blocksLevelConfiguration});
   }
}
