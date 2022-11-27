package iskallia.vault.skill.ability.group;

import iskallia.vault.skill.ability.config.ExecuteConfig;
import iskallia.vault.skill.ability.effect.ExecuteAbility;

public class ExecuteAbilityGroup extends AbilityGroup<ExecuteConfig, ExecuteAbility<ExecuteConfig>> {
   private ExecuteAbilityGroup() {
      super("Execute");
   }

   protected ExecuteConfig getSubConfig(String specialization, int level) {
      return null;
   }

   @Override
   public String getSpecializationName(String specialization) {
      return "Execute";
   }

   public static ExecuteAbilityGroup defaultConfig() {
      ExecuteAbilityGroup group = new ExecuteAbilityGroup();

      for (int i = 0; i < 10; i++) {
         group.addLevel(new ExecuteConfig(1, 1, 10, 1, 1.0F, 100));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return true;
   }
}
