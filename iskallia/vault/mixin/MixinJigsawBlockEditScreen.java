package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.KeyRegistrySuggestions;
import iskallia.vault.util.IKeyRegistrySuggestions;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({JigsawBlockEditScreen.class})
public abstract class MixinJigsawBlockEditScreen extends Screen implements IKeyRegistrySuggestions {
   @Shadow
   private EditBox poolEdit;
   private KeyRegistrySuggestions keyRegistrySuggestions;

   protected MixinJigsawBlockEditScreen(Component pTitle) {
      super(pTitle);
   }

   @Override
   public KeyRegistrySuggestions getSuggestions() {
      return this.keyRegistrySuggestions;
   }

   @Redirect(
      method = {"init"},
      at = @At(
         value = "INVOKE",
         ordinal = 0,
         target = "Lnet/minecraft/client/gui/components/EditBox;setResponder(Ljava/util/function/Consumer;)V"
      )
   )
   public void init(EditBox instance, Consumer<String> pResponder) {
      instance.setResponder(value -> {
         pResponder.accept(value);
         this.keyRegistrySuggestions.updateCommandInfo();
      });
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      Minecraft minecraft = Minecraft.getInstance();
      this.keyRegistrySuggestions = new KeyRegistrySuggestions(minecraft, this, this.poolEdit, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
      this.keyRegistrySuggestions.setAllowSuggestions(true);
      this.keyRegistrySuggestions.updateCommandInfo();
   }

   @Inject(
      method = {"resize"},
      at = {@At("TAIL")}
   )
   public void resize(CallbackInfo ci) {
      this.keyRegistrySuggestions.updateCommandInfo();
   }

   @Inject(
      method = {"keyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void keyPressed(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir) {
      if (this.keyRegistrySuggestions.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
      this.keyRegistrySuggestions.render(pPoseStack, pMouseX, pMouseY);
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
      return this.keyRegistrySuggestions.mouseScrolled(pDelta) || super.mouseScrolled(pMouseX, pMouseY, pDelta);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      return this.keyRegistrySuggestions.mouseClicked(pMouseX, pMouseY, pButton) || super.mouseClicked(pMouseX, pMouseY, pButton);
   }
}
