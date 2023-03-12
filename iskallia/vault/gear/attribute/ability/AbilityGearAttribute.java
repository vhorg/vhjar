package iskallia.vault.gear.attribute.ability;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;

public abstract class AbilityGearAttribute {
   protected final String abilityKey;

   protected AbilityGearAttribute(String abilityKey) {
      this.abilityKey = abilityKey;
   }

   public String getAbilityKey() {
      return this.abilityKey;
   }

   public boolean affectsAbility(String playerAbility) {
      return ModConfigs.ABILITIES.getAbility(playerAbility).map(abilityGroup -> abilityGroup.getParentName().equals(this.abilityKey)).orElse(false);
   }

   public abstract static class AbilityAttributeConfig {
      @Expose
      private String abilityKey;

      protected AbilityAttributeConfig(String abilityKey) {
         this.abilityKey = abilityKey;
      }

      public String getAbilityKey() {
         return this.abilityKey;
      }
   }
}
