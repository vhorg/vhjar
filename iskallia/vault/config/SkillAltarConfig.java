package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class SkillAltarConfig extends Config {
   @Expose
   public float perLevelCost;

   @Override
   public String getName() {
      return "skill_altar";
   }

   @Override
   protected void reset() {
      this.perLevelCost = 0.5F;
   }

   public float getPerLevelCost() {
      return this.perLevelCost;
   }
}
