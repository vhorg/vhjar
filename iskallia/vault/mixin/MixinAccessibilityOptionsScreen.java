package iskallia.vault.mixin;

import iskallia.vault.client.gui.screen.accessibility.VaultAccessibilityScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AccessibilityOptionsScreen.class})
public class MixinAccessibilityOptionsScreen extends Screen {
   protected MixinAccessibilityOptionsScreen(Component pTitle) {
      super(pTitle);
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
