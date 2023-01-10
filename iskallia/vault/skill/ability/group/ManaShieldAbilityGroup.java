package iskallia.vault.skill.ability.group;

import iskallia.vault.skill.ability.config.ManaShieldConfig;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;

public class ManaShieldAbilityGroup extends AbilityGroup<ManaShieldConfig, ManaShieldAbility<ManaShieldConfig>> {
   protected ManaShieldAbilityGroup() {
      super("Mana Shield");
   }

   protected ManaShieldConfig getSubConfig(String specialization, int level) {
      return null;
   }

   @Override
   public String getSpecializationName(String specialization) {
      return "Mana Shield";
   }

   public static ManaShieldAbilityGroup defaultConfig() {
      ManaShieldAbilityGroup group = new ManaShieldAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new ManaShieldConfig(1, 1, 10, 1, 1.25F + i * 0.25F, (i + 1) * 0.2F, 1.0F));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return true;
   }
}
