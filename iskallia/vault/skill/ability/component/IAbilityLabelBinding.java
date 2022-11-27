package iskallia.vault.skill.ability.component;

import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;

@FunctionalInterface
public interface IAbilityLabelBinding<C extends AbstractAbilityConfig> {
   String get(C var1);
}
