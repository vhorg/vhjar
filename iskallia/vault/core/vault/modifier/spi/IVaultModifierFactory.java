package iskallia.vault.core.vault.modifier.spi;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface IVaultModifierFactory<M extends VaultModifier<P>, P> {
   M createVaultModifier(ResourceLocation var1, P var2, VaultModifier.Display var3);
}
