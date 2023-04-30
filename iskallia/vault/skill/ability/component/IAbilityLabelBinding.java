package iskallia.vault.skill.ability.component;

import iskallia.vault.skill.base.Skill;

@FunctionalInterface
public interface IAbilityLabelBinding<C extends Skill> {
   String get(C var1);
}
