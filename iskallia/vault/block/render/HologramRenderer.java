package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.HologramTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class HologramRenderer implements BlockEntityRenderer<HologramTileEntity> {
   private final Font font;
   private final ItemRenderer itemRenderer;

   public HologramRenderer(Context context) {
      this.font = context.getFont();
      this.itemRenderer = Minecraft.getInstance().getItemRenderer();
   }

   public void render(HologramTileEntity entity, float partialTick, PoseStack matrices, MultiBufferSource bufferSource, int light, int overlay) {
      matrices.pushPose();
      matrices.translate(0.5, 0.5, 0.5);
      matrices.scale(-1.0F, -1.0F, 1.0F);
      if (entity.getTree() != null) {
         entity.getTree().render(matrices, bufferSource, partialTick, light, overlay);
      }

      matrices.popPose();
   }

   public boolean shouldRenderOffScreen(HologramTileEntity entity) {
      return true;
   }

   public int getViewDistance() {
      return 128;
   }
}
