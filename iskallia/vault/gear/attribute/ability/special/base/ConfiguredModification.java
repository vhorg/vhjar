package iskallia.vault.gear.attribute.ability.special.base;

public record ConfiguredModification<M extends SpecialAbilityModification<C, V>, C extends SpecialAbilityConfig<V>, V extends SpecialAbilityConfigValue>(
   M modification, V value
) {
}
