package iskallia.vault.client.gui.overlay;

import iskallia.vault.client.gui.helper.ConfettiParticles;
import iskallia.vault.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Deprecated
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
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.CONFETTI_SFX, 1.0F));
      leftConfettiPopper.pop();
      rightConfettiPopper.pop();
   }
}
