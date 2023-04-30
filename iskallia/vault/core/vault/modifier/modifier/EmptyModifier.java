package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class EmptyModifier extends VaultModifier<EmptyModifier.Properties> {
   public static final EmptyModifier INSTANCE = new EmptyModifier(
      VaultMod.id("empty"),
      new EmptyModifier.Properties(),
      new VaultModifier.Display("Empty", TextColor.parseColor("#000000"), "Special empty modifier, do not use")
   );

   private EmptyModifier(ResourceLocation id, EmptyModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   public static class Properties {
   }
}
