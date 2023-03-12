package iskallia.vault.block.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.WardrobeBlock;
import iskallia.vault.block.entity.WardrobeTileEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.client.render.CuriosLayer;

public class WardrobeRenderer implements BlockEntityRenderer<WardrobeTileEntity>, RenderLayerParent<Player, PlayerModel<Player>> {
   private final PlayerModel<Player> steveModel;
   private final List<RenderLayer<Player, PlayerModel<Player>>> layers = Lists.newArrayList();

   public WardrobeRenderer(Context context) {
      this.steveModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
      this.steveModel.young = false;
      this.addLayer(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))
         )
      );
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new ElytraLayer(this, context.getModelSet()));
      this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
      this.addLayer(new CuriosLayer(this));
   }

   private void addLayer(RenderLayer<Player, PlayerModel<Player>> layer) {
      this.layers.add(layer);
   }

   public void render(
      WardrobeTileEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      float floatingYDiff = Mth.sin(blockEntity.getBlockPos().hashCode() + ((float)(System.currentTimeMillis() / 80L % 1000L) + partialTick) / 10.0F) * 0.4F;
      this.steveModel.body.y += floatingYDiff;
      this.steveModel.leftArm.y += floatingYDiff;
      this.steveModel.rightArm.y += floatingYDiff;
      this.steveModel.head.y += floatingYDiff;
      this.steveModel.leftLeg.y += floatingYDiff;
      this.steveModel.rightLeg.y += floatingYDiff;
      poseStack.pushPose();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      MultiBufferSource transparentBufferSource = getBufferSourceForSetTransparency(blockEntity, bufferSource);
      this.renderLayers(blockEntity, partialTick, poseStack, packedLight, transparentBufferSource);
      renderCurios(blockEntity, partialTick, poseStack, packedLight, transparentBufferSource);
      RenderSystem.disableBlend();
      poseStack.popPose();
      this.steveModel.head.y = 0.0F;
      this.steveModel.body.y = 0.0F;
      this.steveModel.leftArm.y = 2.0F;
      this.steveModel.rightArm.y = 2.0F;
      this.steveModel.leftLeg.y = 12.0F;
      this.steveModel.rightLeg.y = 12.0F;
   }

   private static MultiBufferSource getBufferSourceForSetTransparency(WardrobeTileEntity blockEntity, MultiBufferSource bufferSource) {
      return (MultiBufferSource)(blockEntity.shouldRenderSolid()
         ? bufferSource
         : new WardrobeRenderer.TransparentMultiBufferSource(bufferSource, getTransparency(blockEntity)));
   }

   private static void renderCurios(
      WardrobeTileEntity blockEntity, float partialTick, PoseStack poseStack, int packedLight, MultiBufferSource transparentBufferSource
   ) {
      List<ItemStack> stacksToRender = new ArrayList<>();
      blockEntity.getCuriosItems().forEach((slotKey, stacks) -> stacks.forEach((slot, stack) -> {
         if (!stack.isEmpty()) {
            stacksToRender.add(stack);
         }
      }));
      if (!stacksToRender.isEmpty()) {
         poseStack.translate(0.5, 0.4, 0.5);
         poseStack.mulPose(Vector3f.YN.rotationDegrees(((Direction)blockEntity.getBlockState().getValue(WardrobeBlock.FACING)).getOpposite().toYRot()));
         poseStack.scale(0.4F, 0.4F, 0.4F);
         AtomicInteger itemIndex = new AtomicInteger(0);
         float sideDistance = stacksToRender.size() == 1 ? 0.0F : 2.0F / (stacksToRender.size() / 2 + 1);
         boolean isOdd = stacksToRender.size() % 2 == 1;
         ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
         stacksToRender.forEach(stack -> {
            poseStack.pushPose();
            int index = itemIndex.get();
            float floatingY = Mth.sin(index + ((float)(System.currentTimeMillis() / 100L % 1000L) + partialTick) / 10.0F) * 0.03F;
            if (isOdd && index == 0) {
               poseStack.translate(0.0, floatingY, -1.0);
            } else {
               if (isOdd) {
                  index--;
               }

               poseStack.translate(-1.0F + index % 2 * 2, floatingY, -1.0F + sideDistance * (index / 2 + 1));
            }

            itemRenderer.renderStatic(stack, TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, transparentBufferSource, 0);
            poseStack.popPose();
            itemIndex.incrementAndGet();
         });
      }
   }

   private void renderLayers(WardrobeTileEntity blockEntity, float partialTick, PoseStack poseStack, int packedLight, MultiBufferSource transparentBufferSource) {
      poseStack.pushPose();
      poseStack.translate(0.5, 2.05, 0.5);
      poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      poseStack.mulPose(Vector3f.YP.rotationDegrees(((Direction)blockEntity.getBlockState().getValue(WardrobeBlock.FACING)).toYRot()));
      this.layers
         .forEach(
            layer -> layer.render(
               poseStack, transparentBufferSource, packedLight, blockEntity.getDummyRenderPlayer(), 0.0F, 0.0F, partialTick, 0.0F, 0.0F, 0.0F
            )
         );
      poseStack.popPose();
   }

   private static int getTransparency(WardrobeTileEntity blockEntity) {
      Random rand = blockEntity.getLevel().random;
      int regularTransparency = 140;
      if (System.currentTimeMillis() % Math.abs(blockEntity.getBlockPos().hashCode()) < 300L) {
         int error = 20;
         return rand.nextInt(error) + regularTransparency - error / 2;
      } else {
         return regularTransparency;
      }
   }

   public PlayerModel<Player> getModel() {
      return this.steveModel;
   }

   public ResourceLocation getTextureLocation(Player pEntity) {
      return null;
   }

   private static class TransparentMultiBufferSource implements MultiBufferSource {
      private final MultiBufferSource bufferSource;
      private final int alpha;

      public TransparentMultiBufferSource(MultiBufferSource bufferSource, int alpha) {
         this.bufferSource = bufferSource;
         this.alpha = alpha;
      }

      public VertexConsumer getBuffer(RenderType renderType) {
         if (renderType == RenderType.armorEntityGlint() || renderType == RenderType.armorGlint()) {
            return new WardrobeRenderer.TransparentVertexConsumer(this.bufferSource.getBuffer(RenderType.entityGlintDirect()), this.alpha);
         } else if (renderType != RenderType.glintTranslucent() && renderType != RenderType.glint() && renderType != RenderType.glintDirect()) {
            return renderType instanceof CompositeRenderType compositeRenderType
                  && compositeRenderType.state().textureState instanceof TextureStateShard textureStateShard
               ? new WardrobeRenderer.TransparentVertexConsumer(
                  this.bufferSource.getBuffer(textureStateShard.cutoutTexture().<RenderType>map(RenderType::entityTranslucent).orElseGet(() -> renderType)),
                  this.alpha
               )
               : new WardrobeRenderer.TransparentVertexConsumer(this.bufferSource.getBuffer(renderType), this.alpha);
         } else {
            return new WardrobeRenderer.TransparentVertexConsumer(this.bufferSource.getBuffer(renderType), this.alpha);
         }
      }
   }

   private static class TransparentVertexConsumer extends DefaultedVertexConsumer {
      private final VertexConsumer delegate;

      public TransparentVertexConsumer(VertexConsumer delegate, int alpha) {
         this.delegate = delegate;
         this.defaultA = alpha;
      }

      public void vertex(
         float pX,
         float pY,
         float pZ,
         float pRed,
         float pGreen,
         float pBlue,
         float pAlpha,
         float pTexU,
         float pTexV,
         int pOverlayUV,
         int pLightmapUV,
         float pNormalX,
         float pNormalY,
         float pNormalZ
      ) {
         super.vertex(pX, pY, pZ, pRed, pGreen, pBlue, this.defaultA / 256.0F, pTexU, pTexV, pOverlayUV, pLightmapUV, pNormalX, pNormalY, pNormalZ);
      }

      public VertexConsumer vertex(double pX, double pY, double pZ) {
         return this.delegate.vertex(pX, pY, pZ);
      }

      public VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {
         return this.delegate.color(pRed, pGreen, pBlue, this.defaultA);
      }

      public VertexConsumer uv(float pU, float pV) {
         return this.delegate.uv(pU, pV);
      }

      public VertexConsumer overlayCoords(int pU, int pV) {
         return this.delegate.overlayCoords(pU, pV);
      }

      public VertexConsumer uv2(int pU, int pV) {
         return this.delegate.uv2(pU, pV);
      }

      public VertexConsumer normal(float pX, float pY, float pZ) {
         return this.delegate.normal(pX, pY, pZ);
      }

      public void endVertex() {
         this.delegate.endVertex();
      }
   }
}
