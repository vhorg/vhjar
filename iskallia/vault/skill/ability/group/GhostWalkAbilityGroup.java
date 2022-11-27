package iskallia.vault.skill.ability.group;

import iskallia.vault.skill.ability.config.GhostWalkConfig;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;

public class GhostWalkAbilityGroup extends AbilityGroup<GhostWalkConfig, GhostWalkAbility<GhostWalkConfig>> {
   private GhostWalkAbilityGroup() {
      super("Ghost Walk");
   }

   protected GhostWalkConfig getSubConfig(String specialization, int level) {
      return null;
   }

   @Override
   public String getSpecializationName(String specialization) {
      return "Ghost Walk";
   }

   public static GhostWalkAbilityGroup defaultConfig() {
      GhostWalkAbilityGroup group = new GhostWalkAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new GhostWalkConfig(1, 1, 10, 0, 10.0F, 10));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return true;
   }
}
