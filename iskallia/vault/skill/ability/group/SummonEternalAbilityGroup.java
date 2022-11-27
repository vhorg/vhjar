package iskallia.vault.skill.ability.group;

import iskallia.vault.skill.ability.config.SummonEternalConfig;
import iskallia.vault.skill.ability.effect.SummonEternalAbility;

public class SummonEternalAbilityGroup extends AbilityGroup<SummonEternalConfig, SummonEternalAbility<SummonEternalConfig>> {
   private SummonEternalAbilityGroup() {
      super("Summon Eternal");
   }

   protected SummonEternalConfig getSubConfig(String specialization, int level) {
      return null;
   }

   @Override
   public String getSpecializationName(String specialization) {
      return "Summon Eternal";
   }

   public static SummonEternalAbilityGroup defaultConfig() {
      SummonEternalAbilityGroup group = new SummonEternalAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new SummonEternalConfig(1, 1, 10, 1, 10.0F, 1, 200, 0.5F, true));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return true;
   }
}
