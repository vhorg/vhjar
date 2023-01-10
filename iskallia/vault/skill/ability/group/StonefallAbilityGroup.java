package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.StonefallConfig;
import iskallia.vault.skill.ability.config.sub.StonefallColdConfig;
import iskallia.vault.skill.ability.config.sub.StonefallSnowConfig;
import iskallia.vault.skill.ability.effect.StonefallAbility;
import java.util.ArrayList;
import java.util.List;

public class StonefallAbilityGroup extends AbilityGroup<StonefallConfig, StonefallAbility> {
   @Expose
   private final List<StonefallSnowConfig> snowLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<StonefallColdConfig> coldLevelConfiguration = new ArrayList<>();

   protected StonefallAbilityGroup() {
      super("Stonefall");
   }

   protected StonefallConfig getSubConfig(String specialization, int level) {
      return (StonefallConfig)(switch (specialization) {
         case "Stonefall_Snow" -> (StonefallSnowConfig)this.snowLevelConfiguration.get(level);
         case "Stonefall_Cold" -> (StonefallColdConfig)this.coldLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Stonefall_Snow" -> "Surefoot";
         case "Stonefall_Cold" -> "Coldsnap";
         default -> "Stonefall";
      };
   }

   public static StonefallAbilityGroup defaultConfig() {
      StonefallAbilityGroup group = new StonefallAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new StonefallConfig(1, 1, 400 - 40 * i, 0, 10 + 2 * i, 40));
         group.snowLevelConfiguration.add(new StonefallSnowConfig(1, 1, 20, 0, 1.0F, 4.0F));
         group.coldLevelConfiguration.add(new StonefallColdConfig(1, 1, 20, 0, 1.0F, 100));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.snowLevelConfiguration, this.coldLevelConfiguration});
   }
}
