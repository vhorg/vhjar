package iskallia.vault.skill.ability.component;

import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;

public record AbilityLabelContext<C extends AbstractAbilityConfig>(C config, int vaultLevel) {
}
