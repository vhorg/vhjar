package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.task.renderer.context.GodAltarRendererContext;
import iskallia.vault.world.data.GodAltarData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class GodAltarOverlay implements IIngameOverlay {
   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      if (!ModKeybinds.bountyStatusKey.isDown()) {
         matrixStack.pushPose();
         matrixStack.translate(-4.0, height / 2.0F - 23.0F, 0.0);
         matrixStack.scale(0.8F, 0.8F, 0.8F);

         for (GodAltarData.Entry entry : GodAltarData.CLIENT) {
            GodAltarRendererContext context = GodAltarRendererContext.forHud(matrixStack, partialTick, gui.getFont(), entry.getGod());
            entry.getTask().render(context);
         }

         matrixStack.popPose();
      }
   }
}
