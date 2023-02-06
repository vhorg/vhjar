package iskallia.vault.core.vault.abyss;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class AbyssVaultEffectModifier extends VaultModifier<AbyssVaultEffectModifier.Properties> {
   public static final ResourceLocation ID = VaultMod.id("abyss_effect");
   public static final AbyssVaultEffectModifier INSTANCE = new AbyssVaultEffectModifier(
      ID, new AbyssVaultEffectModifier.Properties(0.2F), new VaultModifier.Display("%d%% Abyssal", TextColor.parseColor("#00724C"), "")
   );

   public AbyssVaultEffectModifier(ResourceLocation id, AbyssVaultEffectModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> "");
      this.setNameFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getEffect() * s * 100.0F)));
   }

   @Override
   public Component getChatDisplayNameComponent(int modifierStackSize) {
      return super.getNameComponentFormatted(modifierStackSize);
   }

   public static class Properties {
      @Expose
      private float effect;

      public Properties(float effect) {
         this.effect = effect;
      }

      public float getEffect() {
         return this.effect;
      }
   }
}
