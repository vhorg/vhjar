package iskallia.vault.gear.attribute.ability;

import com.google.gson.annotations.Expose;

public abstract class AbilityGearAttribute {
   protected final String abilityKey;

   protected AbilityGearAttribute(String abilityKey) {
      this.abilityKey = abilityKey;
   }

   public String getAbilityKey() {
      return this.abilityKey;
   }

   public boolean affectsAbility(String playerAbility) {
      return this.abilityKey.equals(playerAbility);
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
