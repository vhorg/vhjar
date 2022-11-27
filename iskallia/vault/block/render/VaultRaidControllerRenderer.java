package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class VaultRaidControllerRenderer implements BlockEntityRenderer<VaultRaidControllerTileEntity> {
   private final Font font;

   public VaultRaidControllerRenderer(Context context) {
      this.font = context.getFont();
   }

   public void render(
      VaultRaidControllerTileEntity te, float partialTicks, PoseStack renderStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      if (!te.didTriggerRaid()) {
         this.drawHoveringModifiers(te.getModifierDisplay(), partialTicks, renderStack, buffer, combinedLight);
      }
   }

   private void drawHoveringModifiers(List<Component> modifiers, float pTicks, PoseStack renderStack, MultiBufferSource buffer, int combinedLight) {
      EntityRenderDispatcher mgr = Minecraft.getInstance().getEntityRenderDispatcher();
      Font fr = this.font;
      renderStack.pushPose();
      renderStack.translate(0.5, 2.5, 0.5);
      renderStack.mulPose(mgr.cameraOrientation());
      renderStack.scale(-0.025F, -0.025F, 0.025F);
      Matrix4f matr = renderStack.last().pose();
      float textBgOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
      int textBgAlpha = (int)(textBgOpacity * 255.0F) << 24;

      for (Component modifier : modifiers) {
         float xShift = fr.width(modifier) / 2.0F;
         fr.drawInBatch(modifier, -xShift, 0.0F, 553648127, false, matr, buffer, true, textBgAlpha, combinedLight);
         fr.drawInBatch(modifier, -xShift, 0.0F, -1, false, matr, buffer, false, 0, combinedLight);
         renderStack.translate(0.0, -10.0, 0.0);
      }

      renderStack.popPose();
   }

   private boolean isInDrawDistance(BlockPos pos) {
      EntityRenderDispatcher mgr = Minecraft.getInstance().getEntityRenderDispatcher();
      return mgr.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 4096.0;
   }
}
