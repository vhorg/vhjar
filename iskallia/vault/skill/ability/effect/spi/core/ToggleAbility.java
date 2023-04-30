package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.base.SkillContext;

public abstract class ToggleAbility extends Ability {
   public ToggleAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks);
   }

   protected ToggleAbility() {
   }

   @Override
   public void onKeyUp(SkillContext context) {
      if (this.isUnlocked()) {
         this.setActive(!this.isActive());
         Ability.ActionResult result = this.onAction(context);
         if (result.startCooldown()) {
            this.putOnCooldown(result.getCooldownDelayTicks(), context);
         }
      }
   }

   public Ability.ActionResult onAction(SkillContext context) {
      Ability.ActionResult result;
      if (this.canToggle(context)) {
         result = this.doToggle(context);
         if (result.isSuccess()) {
            this.doToggleParticles(context);
            this.doToggleSound(context);
         }
      } else {
         result = Ability.ActionResult.fail();
      }

      return result;
   }

   protected boolean canToggle(SkillContext context) {
      return true;
   }

   protected Ability.ActionResult doToggle(SkillContext context) {
      return this.isActive() ? Ability.ActionResult.successCooldownDeferred() : Ability.ActionResult.successCooldownImmediate();
   }

   protected void doToggleParticles(SkillContext context) {
   }

   protected void doToggleSound(SkillContext context) {
   }
}
