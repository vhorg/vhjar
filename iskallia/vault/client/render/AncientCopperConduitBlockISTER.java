package iskallia.vault.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.AncientCopperConduitBlock;
import iskallia.vault.block.render.AncientCopperConduitRenderer;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.entity.model.ModModelLayers;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;

public class AncientCopperConduitBlockISTER extends BlockEntityWithoutLevelRenderer {
   private final BlockEntityRenderDispatcher blockEntityRenderer;
   private final ModelPart eye;
   private final ModelPart wind;
   private final ModelPart cage;
   public static final IItemRenderProperties INSTANCE = new IItemRenderProperties() {
      final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
         () -> new AncientCopperConduitBlockISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
      );

      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
         return (BlockEntityWithoutLevelRenderer)this.renderer.get();
      }
   };

   public AncientCopperConduitBlockISTER(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
      super(pBlockEntityRenderDispatcher, pEntityModelSet);
      this.blockEntityRenderer = pBlockEntityRenderDispatcher;
      this.eye = pEntityModelSet.bakeLayer(ModModelLayers.ANCIENT_COPPER_CONDUIT_BLOCK_EYE);
      this.wind = pEntityModelSet.bakeLayer(ModModelLayers.ANCIENT_COPPER_CONDUIT_BLOCK_WIND);
      this.cage = pEntityModelSet.bakeLayer(ModModelLayers.ANCIENT_COPPER_CONDUIT_BLOCK_CAGE);
   }

   public float getActiveRotation(float p_59198_) {
      return ((float)ClientScheduler.INSTANCE.getTickCount() + p_59198_) * -0.0375F;
   }

   public void renderByItem(ItemStack stack, TransformType type, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AncientCopperConduitBlock) {
         pPackedLight = 15728880;
         float f = (float)ClientScheduler.INSTANCE.getTickCount() + Minecraft.getInstance().getFrameTime();
         float f1 = this.getActiveRotation(Minecraft.getInstance().getFrameTime()) * (180.0F / (float)Math.PI);
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
         vector3f.normalize();
         pPoseStack.pushPose();
         pPoseStack.mulPose(vector3f.rotationDegrees(f1));
         this.cage
            .render(pPoseStack, AncientCopperConduitRenderer.CONDUIT_OUTTER.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.pushPose();
         float scale = 1.5F;
         pPoseStack.scale(scale, scale, scale);
         pPoseStack.pushPose();
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         this.cage
            .render(pPoseStack, AncientCopperConduitRenderer.CONDUIT_OUTTER.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.pushPose();
         pPoseStack.scale(1.1F, 1.1F, 1.1F);
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         this.cage
            .render(
               pPoseStack, AncientCopperConduitRenderer.CONDUIT_OUTTER2.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay
            );
         pPoseStack.popPose();
         pPoseStack.popPose();
         pPoseStack.popPose();
         int i = 1;
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         VertexConsumer vertexconsumer = AncientCopperConduitRenderer.CONDUIT_WIND_VERTICAL.buffer(pBufferSource, RenderType::entityCutoutNoCull);
         this.wind.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         pPoseStack.scale(0.875F, 0.875F, 0.875F);
         vertexconsumer = AncientCopperConduitRenderer.CONDUIT_WIND.buffer(pBufferSource, RenderType::entityCutoutNoCull);
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         this.wind.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         Camera camera = this.blockEntityRenderer.camera;
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         pPoseStack.scale(0.5F, 0.5F, 0.5F);
         float f3 = -camera.getYRot();
         pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         float f4 = 1.3333334F;
         pPoseStack.scale(f4, f4, f4);
         this.eye
            .render(pPoseStack, AncientCopperConduitRenderer.CONDUIT_CENTER.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
      }
   }

   public static void registerISTER(
      Consumer<IItemRenderProperties> consumer, final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory
   ) {
      consumer.accept(
         new IItemRenderProperties() {
            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
               () -> factory.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
            );

            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
               return (BlockEntityWithoutLevelRenderer)this.renderer.get();
            }
         }
      );
   }
}
