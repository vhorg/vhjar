package iskallia.vault.gear.attribute.ability.special.base;

public record ConfiguredModification<C extends SpecialAbilityModification.Config<C>, T extends SpecialAbilityModification<C>>(C config, T modification) {
}
