package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.world.entity.LivingEntity;

public abstract class LowHealthTalent extends PlayerTalent {
   @Expose
   private float healthThreshold;

   public LowHealthTalent(int cost, float healthThreshold) {
      super(cost);
      this.healthThreshold = healthThreshold;
   }

   public LowHealthTalent(int cost, int levelRequirement, float healthThreshold) {
      super(cost, levelRequirement);
      this.healthThreshold = healthThreshold;
   }

   public float getHealthThreshold() {
      return this.healthThreshold;
   }

   public boolean shouldGetBenefits(LivingEntity entity) {
      return entity.getHealth() / entity.getMaxHealth() < this.getHealthThreshold();
   }
}
