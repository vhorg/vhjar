package iskallia.vault.entity.ai;

import net.minecraft.entity.LivingEntity;

public class RegenAfterAWhile<T extends LivingEntity> {
   private final int startTicksUntilRegen;
   private final int ticksUntilPulse;
   private final float regenPercentage;
   public T entity;
   public int ticksUntilRegen;
   public int ticksUntilNextPulse;

   public RegenAfterAWhile(T entity) {
      this(entity, 90, 10, 0.05F);
   }

   public RegenAfterAWhile(T entity, int startTicksUntilRegen, int ticksUntilPulse, float regenPercentage) {
      this.entity = entity;
      this.startTicksUntilRegen = startTicksUntilRegen;
      this.ticksUntilPulse = ticksUntilPulse;
      this.regenPercentage = regenPercentage;
   }

   private void resetTicks() {
      this.ticksUntilRegen = this.startTicksUntilRegen;
      this.resetPulseTicks();
   }

   private void resetPulseTicks() {
      this.ticksUntilNextPulse = this.ticksUntilPulse;
   }

   public void onDamageTaken() {
      this.resetTicks();
   }

   public void tick() {
      if (this.ticksUntilRegen <= 0) {
         if (this.ticksUntilNextPulse <= 0) {
            float maxHealth = this.entity.func_110138_aP();
            float currentHealth = this.entity.func_110143_aJ();
            this.entity.func_70606_j(Math.min(maxHealth, currentHealth + maxHealth * this.regenPercentage));
            this.resetPulseTicks();
         } else {
            this.ticksUntilNextPulse--;
         }
      } else {
         this.ticksUntilRegen--;
      }
   }
}
