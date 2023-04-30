package iskallia.vault.mixin;

import iskallia.vault.client.gui.screen.accessibility.VaultAccessibilityScreen;
import iskallia.vault.client.render.IVaultOptions;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AccessibilityOptionsScreen.class})
public class MixinAccessibilityOptionsScreen extends Screen {
   @Final
   @Shadow
   @Mutable
   private static Option[] OPTIONS;
   private static final Component VANILLA_POTION_DAMAGE_EFFECTS_TOOLTIP = new TranslatableComponent(
      "options.the_vault.accessibility.vanilla_potion_damage_effects.tooltip"
   );
   private static final CycleOption<Boolean> VANILLA_POTION_DAMAGE_EFFECTS = CycleOption.createOnOff(
      "options.the_vault.accessibility.vanilla_potion_damage_effects",
      VANILLA_POTION_DAMAGE_EFFECTS_TOOLTIP,
      options -> ((IVaultOptions)options).doVanillaPotionDamageEffects(),
      (options, option, value) -> ((IVaultOptions)options).setVanillaPotionDamageEffects(value)
   );

   protected MixinAccessibilityOptionsScreen(Component pTitle) {
      super(pTitle);
   }

   @Inject(
      method = {"<clinit>"},
      at = {@At("TAIL")}
   )
   private static void addVaultOptions(CallbackInfo ci) {
      Option[] extendedOptions = new Option[OPTIONS.length + 1];
      System.arraycopy(OPTIONS, 0, extendedOptions, 0, OPTIONS.length);
      extendedOptions[OPTIONS.length] = VANILLA_POTION_DAMAGE_EFFECTS;
      OPTIONS = extendedOptions;
   }

   @Inject(
      method = {"createFooter"},
      at = {@At("TAIL")}
   )
   protected void addVaultAccessibilityButton(CallbackInfo ci) {
      Button accessibiltyScreenButton = new Button(
         this.width - 136, 6, 130, 20, new TextComponent("Vault Hunters Options"), button -> Minecraft.getInstance().setScreen(new VaultAccessibilityScreen())
      );
      this.addRenderableWidget(accessibiltyScreenButton);
   }
}
