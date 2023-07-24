package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.base.SkillContext;

public abstract class HoldAbility extends Ability {
   public HoldAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks);
   }

   protected HoldAbility() {
   }

   @Override
   public boolean onKeyDown(SkillContext context) {
      if (!super.onKeyDown(context)) {
         return false;
      } else {
         Ability.ActionResult result = this.onActionBegin(context);
         if (result.startCooldown()) {
            this.setActive(false);
            this.putOnCooldown(context);
         } else if (result.isSuccess()) {
            this.setActive(true);
         }

         return true;
      }
   }

   @Override
   public boolean onKeyUp(SkillContext context) {
      if (!super.onKeyUp(context)) {
         return false;
      } else {
         if (this.isActive()) {
            this.setActive(false);
            Ability.ActionResult result = this.onActionEnd(context);
            if (result.startCooldown()) {
               this.putOnCooldown(result.getCooldownDelayTicks(), context);
            }
         }

         return true;
      }
   }

   @Override
   public boolean onCancelKeyDown(SkillContext context) {
      if (!super.onCancelKeyDown(context)) {
         return false;
      } else {
         this.setActive(false);
         return true;
      }
   }

   @Override
   public void onBlur(SkillContext context) {
      super.onBlur(context);
      this.setActive(false);
   }

   public Ability.ActionResult onActionBegin(SkillContext context) {
      Ability.ActionResult result;
      if (this.canBeginHold(context)) {
         result = this.doHoldBeginAction(context);
         if (result.isSuccess()) {
            this.doHoldBeginParticles(context);
            this.doHoldBeginSound(context);
         }
      } else {
         result = Ability.ActionResult.fail();
      }

      return result;
   }

   public Ability.ActionResult onActionEnd(SkillContext context) {
      Ability.ActionResult result = this.doHoldEndAction(context);
      if (result.isSuccess()) {
         this.doHoldEndParticles(context);
         this.doHoldEndSound(context);
      }

      return result;
   }

   protected boolean canBeginHold(SkillContext context) {
      return true;
   }

   protected Ability.ActionResult doHoldBeginAction(SkillContext context) {
      return Ability.ActionResult.successCooldownDeferred();
   }

   protected void doHoldBeginParticles(SkillContext context) {
   }

   protected void doHoldBeginSound(SkillContext context) {
   }

   protected Ability.ActionResult doHoldEndAction(SkillContext context) {
      return Ability.ActionResult.successCooldownImmediate();
   }

   protected void doHoldEndParticles(SkillContext context) {
   }

   protected void doHoldEndSound(SkillContext context) {
   }
}
