package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.ConfettiParticles;
import iskallia.vault.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GiftBombOverlay {
   private static ConfettiParticles leftConfettiPopper = new ConfettiParticles()
      .angleRange(290.0F, 355.0F)
      .quantityRange(60, 80)
      .delayRange(0, 10)
      .lifespanRange(20, 100)
      .sizeRange(2, 5)
      .speedRange(2.0F, 10.0F);
   private static ConfettiParticles rightConfettiPopper = new ConfettiParticles()
      .angleRange(200.0F, 265.0F)
      .quantityRange(60, 80)
      .delayRange(0, 10)
      .lifespanRange(20, 100)
      .sizeRange(2, 5)
      .speedRange(2.0F, 10.0F);

   @OnlyIn(Dist.CLIENT)
   public static void pop() {
      Minecraft.func_71410_x().func_147118_V().func_147682_a(SimpleSound.func_184371_a(ModSounds.CONFETTI_SFX, 1.0F));
      leftConfettiPopper.pop();
      rightConfettiPopper.pop();
   }

   @SubscribeEvent
   public static void onPostRender(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         Minecraft minecraft = Minecraft.func_71410_x();
         MatrixStack matrixStack = event.getMatrixStack();
         int width = minecraft.func_228018_at_().func_198107_o();
         int height = minecraft.func_228018_at_().func_198087_p();
         int midX = width / 2;
         int midY = height / 2;
         leftConfettiPopper.spawnedPosition(10, midY);
         rightConfettiPopper.spawnedPosition(width - 10, midY);
         leftConfettiPopper.tick();
         rightConfettiPopper.tick();
         leftConfettiPopper.render(matrixStack);
         rightConfettiPopper.render(matrixStack);
      }
   }
}
