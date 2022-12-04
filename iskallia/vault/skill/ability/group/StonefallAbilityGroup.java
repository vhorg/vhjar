package iskallia.vault.skill.ability.group;

import iskallia.vault.skill.ability.config.StonefallConfig;
import iskallia.vault.skill.ability.effect.StonefallAbility;

public class StonefallAbilityGroup extends AbilityGroup<StonefallConfig, StonefallAbility<StonefallConfig>> {
   protected StonefallAbilityGroup() {
      super("Stonefall");
   }

   protected StonefallConfig getSubConfig(String specialization, int level) {
      return null;
   }

   @Override
   public String getSpecializationName(String specialization) {
      return "Stonefall";
   }

   public static StonefallAbilityGroup defaultConfig() {
      StonefallAbilityGroup group = new StonefallAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new StonefallConfig(1, 1, 400 - 40 * i, 0, 10 + 2 * i, 40));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return true;
   }
}
