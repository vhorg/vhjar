package iskallia.vault.world.vault.modifier.spi;

import net.minecraft.resources.ResourceLocation;

public interface IVaultModifierStack {
   VaultModifier<?> getModifier();

   ResourceLocation getModifierId();

   int getSize();

   boolean isEmpty();
}
