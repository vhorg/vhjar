package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import java.util.Optional;

public interface CooldownSkill {
   Optional<Cooldown> getCooldown();

   default Optional<Cooldown> getTreeCooldown() {
      return this instanceof Skill skill && skill.getParent() instanceof CooldownSkill parent ? parent.getCooldown() : this.getCooldown();
   }

   void putOnCooldown(int var1, SkillContext var2);

   default void putOnCooldown(SkillContext context) {
      this.putOnCooldown(0, context);
   }

   default boolean isOnCooldown() {
      return this.getCooldown().map(cooldown -> cooldown.remainingTicks > 0).orElse(false);
   }

   default boolean isTreeOnCooldown() {
      return this instanceof Skill skill && skill.getParent() instanceof CooldownSkill parent ? parent.isOnCooldown() : this.isOnCooldown();
   }
}
