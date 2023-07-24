package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.base.SkillContext;

public abstract class InstantAbility extends Ability {
   public InstantAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks);
   }

   protected InstantAbility() {
   }

   @Override
   public boolean onKeyUp(SkillContext context) {
      if (!super.onKeyUp(context)) {
         return false;
      } else {
         Ability.ActionResult result = this.onAction(context);
         if (result.startCooldown()) {
            this.putOnCooldown(result.getCooldownDelayTicks(), context);
         }

         return true;
      }
   }

   public Ability.ActionResult onAction(SkillContext context) {
      if (this.canDoAction(context)) {
         Ability.ActionResult result = this.doAction(context);
         if (result.isSuccess()) {
            this.doActionPost(context);
            this.doParticles(context);
            this.doSound(context);
            return result;
         }
      }

      return Ability.ActionResult.fail();
   }

   protected boolean canDoAction(SkillContext context) {
      return true;
   }

   protected Ability.ActionResult doAction(SkillContext context) {
      return Ability.ActionResult.fail();
   }

   protected void doActionPost(SkillContext context) {
   }

   protected void doParticles(SkillContext context) {
   }

   protected void doSound(SkillContext context) {
   }
}
