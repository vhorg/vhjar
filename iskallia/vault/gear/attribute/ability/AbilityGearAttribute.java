package iskallia.vault.gear.attribute.ability;

import com.google.gson.annotations.Expose;
import javax.annotation.Nullable;

public abstract class AbilityGearAttribute {
   protected final String abilityKey;

   protected AbilityGearAttribute(String abilityKey) {
      this.abilityKey = abilityKey;
   }

   public String getAbilityKey() {
      return this.abilityKey;
   }

   public boolean affectsAbility(@Nullable String abilityKey) {
      return this.abilityKey.equals(abilityKey);
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
