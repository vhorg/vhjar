package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.FarmerConfig;
import iskallia.vault.skill.ability.config.sub.FarmerAnimalConfig;
import iskallia.vault.skill.ability.config.sub.FarmerCactusConfig;
import iskallia.vault.skill.ability.config.sub.FarmerMelonConfig;
import iskallia.vault.skill.ability.effect.FarmerAbility;
import java.util.ArrayList;
import java.util.List;

public class FarmerAbilityGroup extends AbilityGroup<FarmerConfig, FarmerAbility<FarmerConfig>> {
   @Expose
   private final List<FarmerMelonConfig> melonLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<FarmerCactusConfig> cactusLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<FarmerAnimalConfig> animalLevelConfiguration = new ArrayList<>();

   protected FarmerAbilityGroup() {
      super("Farmer");
   }

   protected FarmerConfig getSubConfig(String specialization, int level) {
      return (FarmerConfig)(switch (specialization) {
         case "Farmer_Melon" -> (FarmerMelonConfig)this.melonLevelConfiguration.get(level);
         case "Farmer_Cactus" -> (FarmerCactusConfig)this.cactusLevelConfiguration.get(level);
         case "Farmer_Animal" -> (FarmerAnimalConfig)this.animalLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Farmer_Melon" -> "Cultivator";
         case "Farmer_Cactus" -> "Gardener";
         case "Farmer_Animal" -> "Rancher";
         default -> "Farmer";
      };
   }

   public static FarmerAbilityGroup defaultConfig() {
      FarmerAbilityGroup group = new FarmerAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new FarmerConfig(1, 1, 10, 1, 5.0F, 5 - i, 3 + i, 2));
         group.melonLevelConfiguration.add(new FarmerMelonConfig(1, 1, 10, 1, 5.0F, 5 - i, 3 + i, 2));
         group.cactusLevelConfiguration.add(new FarmerCactusConfig(1, 1, 10, 1, 5.0F, 5 - i, 3 + i, 2));
         group.animalLevelConfiguration.add(new FarmerAnimalConfig(1, 1, 10, 1, 5.0F, 5 - i, 3 + i, 2, 0.05F));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(
         this.levelConfiguration, new List[]{this.melonLevelConfiguration, this.cactusLevelConfiguration, this.animalLevelConfiguration}
      );
   }
}
