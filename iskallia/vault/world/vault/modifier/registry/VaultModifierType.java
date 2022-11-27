package iskallia.vault.world.vault.modifier.registry;

import iskallia.vault.world.vault.modifier.spi.IVaultModifierFactory;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;

public record VaultModifierType<M extends VaultModifier<P>, P>(Class<M> modifierClass, Class<P> modifierPropertyClass, IVaultModifierFactory<M, P> factory) {
   public static <M extends VaultModifier<P>, P> VaultModifierType<M, P> of(
      Class<M> modifierClass, Class<P> modifierPropertyClass, IVaultModifierFactory<M, P> factory
   ) {
      return new VaultModifierType<>(modifierClass, modifierPropertyClass, factory);
   }
}
