package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.RampageConfig;
import iskallia.vault.skill.ability.config.sub.RampageChainConfig;
import iskallia.vault.skill.ability.config.sub.RampageLeechConfig;
import iskallia.vault.skill.ability.effect.RampageAbility;
import java.util.ArrayList;
import java.util.List;

public class RampageAbilityGroup extends AbilityGroup<RampageConfig, RampageAbility> {
   @Expose
   private final List<RampageLeechConfig> leechLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<RampageChainConfig> chainLevelConfiguration = new ArrayList<>();

   private RampageAbilityGroup() {
      super("Rampage");
   }

   protected RampageConfig getSubConfig(String specialization, int level) {
      return (RampageConfig)(switch (specialization) {
         case "Rampage_Leech" -> (RampageLeechConfig)this.leechLevelConfiguration.get(level);
         case "Rampage_Chain" -> (RampageChainConfig)this.chainLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Rampage_Leech" -> "Vampire";
         case "Rampage_Chain" -> "Chain";
         default -> "Rampage";
      };
   }

   public static RampageAbilityGroup defaultConfig() {
      RampageAbilityGroup group = new RampageAbilityGroup();

      for (int i = 0; i < 10; i++) {
         group.addLevel(new RampageConfig(1, 1, 10, 1, 1.0F, 1 + i));
         group.leechLevelConfiguration.add(new RampageLeechConfig(1, 1, 10, 1, 1.0F, 0.9F));
         group.chainLevelConfiguration.add(new RampageChainConfig(1, 1, 10, 1, 1.0F, 5));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.leechLevelConfiguration, this.chainLevelConfiguration});
   }
}
