package iskallia.vault.block.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.base.GodAltarBlock;
import iskallia.vault.block.base.GodAltarTileEntity;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.task.renderer.context.GodAltarRendererContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class GodAltarRenderer implements BlockEntityRenderer<GodAltarTileEntity> {
   private final Font font;

   public GodAltarRenderer(Context context) {
      this.font = context.getFont();
   }

   public void render(
      GodAltarTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlayIn
   ) {
      RenderSystem.enableDepthTest();
      matrixStack.pushPose();
      float scale = 0.012F;
      matrixStack.translate(0.5, 2.1, 0.5);
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      matrixStack.translate(-65.0, -11.0, 0.0);
      if (tileEntity.getTask() != null) {
         GodAltarRendererContext context = GodAltarRendererContext.forWorld(
            matrixStack, partialTicks, this.font, (VaultGod)tileEntity.getBlockState().getValue(GodAltarBlock.GOD)
         );
         tileEntity.getTask().render(context);
      }

      matrixStack.popPose();
   }
}
