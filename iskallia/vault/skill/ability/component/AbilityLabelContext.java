package iskallia.vault.skill.ability.component;

import iskallia.vault.skill.base.Skill;

public record AbilityLabelContext<C extends Skill>(C config, int vaultLevel) {
}
