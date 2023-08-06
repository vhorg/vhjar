package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.entity.FallingSootEntity;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class FallingSootRenderer extends EntityRenderer<FallingSootEntity> {
   public FallingSootRenderer(Context p_174420_) {
      super(p_174420_);
      this.shadowRadius = 0.5F;
   }

   public void render(FallingSootEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      BlockState blockstate = pEntity.getBlockState();
      if (blockstate.getRenderShape() == RenderShape.MODEL) {
         Level level = pEntity.getLevel();
         BlockPos pos = pEntity.blockPosition();
         boolean onSpawn = Math.abs(pEntity.getY() - pos.getY()) < 0.02 && pEntity.tickCount < 0 && blockstate != level.getBlockState(pos);
         if (!onSpawn && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            pMatrixStack.pushPose();
            BlockPos blockpos = new BlockPos(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
            pMatrixStack.translate(-0.5, 0.0, -0.5);
            BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();

            for (RenderType type : RenderType.chunkBufferLayers()) {
               if (ItemBlockRenderTypes.canRenderInLayer(blockstate, type)) {
                  ForgeHooksClient.setRenderType(type);
                  blockrenderdispatcher.getModelRenderer()
                     .tesselateBlock(
                        level,
                        blockrenderdispatcher.getBlockModel(blockstate),
                        blockstate,
                        blockpos,
                        pMatrixStack,
                        pBuffer.getBuffer(type),
                        false,
                        new Random(),
                        blockstate.getSeed(pEntity.getStartPos()),
                        OverlayTexture.NO_OVERLAY
                     );
               }
            }

            ForgeHooksClient.setRenderType(null);
            pMatrixStack.popPose();
            super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
         }
      }
   }

   public ResourceLocation getTextureLocation(FallingSootEntity pEntity) {
      return null;
   }
}
