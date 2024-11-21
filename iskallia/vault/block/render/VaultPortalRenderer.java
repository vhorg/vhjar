package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.client.util.ClientScheduler;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

public class VaultPortalRenderer implements BlockEntityRenderer<VaultPortalTileEntity> {
   private final BlockRenderDispatcher brd;

   public VaultPortalRenderer(Context context) {
      this.brd = context.getBlockRenderDispatcher();
   }

   public void render(VaultPortalTileEntity tile, float pPartialTick, PoseStack matrixStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
      BlockState state = tile.getBlockState();
      BakedModel bakedmodel = this.brd.getBlockModel(state);
      int color = tile.getData().map(data -> data.getModel().getBlockColor(data, (float)ClientScheduler.INSTANCE.getTick() + pPartialTick)).orElse(16777215);
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      VertexConsumer buf = buffers.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
      this.brd.getModelRenderer().renderModel(matrixStack.last(), buf, state, bakedmodel, r, g, b, packedLight, packedOverlay, EmptyModelData.INSTANCE);
   }
}
