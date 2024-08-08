package iskallia.vault.item.tool;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class SpecialItemRenderer extends BlockEntityWithoutLevelRenderer {
   public SpecialItemRenderer() {
      super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
   }

   public void renderModel(
      ModelResourceLocation location,
      int tint,
      ItemStack stack,
      TransformType transformType,
      PoseStack matrices,
      MultiBufferSource buffer,
      int light,
      int overlay,
      Boolean foil
   ) {
      this.renderModel(location, tint, stack, transformType, matrices, buffer, light, overlay, foil, () -> {});
   }

   public void renderModel(
      ModelResourceLocation location,
      int tint,
      ItemStack stack,
      TransformType transformType,
      PoseStack matrices,
      MultiBufferSource buffer,
      int light,
      int overlay,
      Boolean foil,
      Runnable runnable
   ) {
      BakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
      if (transformType == TransformType.GUI && !model.usesBlockLight()) {
         Lighting.setupForFlatItems();
      }

      matrices.pushPose();
      matrices.translate(0.5, 0.5, 0.5);
      model = ForgeHooksClient.handleCameraTransforms(
         matrices, model, transformType, transformType == TransformType.FIRST_PERSON_LEFT_HAND || transformType == TransformType.THIRD_PERSON_LEFT_HAND
      );
      matrices.translate(-0.5, -0.5, -0.5);
      runnable.run();
      if (model.isLayered()) {
         for (Pair<BakedModel, RenderType> layerModel : model.getLayerModels(stack, true)) {
            BakedModel layer = (BakedModel)layerModel.getFirst();
            RenderType renderType = (RenderType)layerModel.getSecond();
            ForgeHooksClient.setRenderType(renderType);
            VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(buffer, renderType, true, foil == null ? stack.hasFoil() : foil);
            Minecraft.getInstance().getItemRenderer().renderModelLists(layer, stack, light, overlay, matrices, consumer);
            if (buffer instanceof BufferSource src) {
               src.endBatch(renderType);
            }
         }

         ForgeHooksClient.setRenderType(null);
      } else {
         RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
         VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(buffer, renderType, true, foil == null ? stack.hasFoil() : foil);
         this.renderModelLists(model, tint, matrices, consumer, light, overlay);
         if (buffer instanceof BufferSource src) {
            src.endBatch(renderType);
         }
      }

      if (transformType == TransformType.GUI && !model.usesBlockLight()) {
         Lighting.setupFor3DItems();
      }

      matrices.popPose();
      Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
      atlas.apply(new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
   }

   public void renderModelLists(BakedModel model, int tint, PoseStack matrices, VertexConsumer buffer, int light, int overlay) {
      Random random = new Random();

      for (Direction direction : Direction.values()) {
         this.renderQuadList(model.getQuads(null, direction, random), tint, matrices, buffer, light, overlay);
      }

      this.renderQuadList(model.getQuads(null, null, random), tint, matrices, buffer, light, overlay);
   }

   public void renderQuadList(List<BakedQuad> quads, int tint, PoseStack matrices, VertexConsumer buffer, int light, int overlay) {
      Pose matrix = matrices.last();

      for (BakedQuad quad : quads) {
         float red = (tint >> 16 & 0xFF) / 255.0F;
         float green = (tint >> 8 & 0xFF) / 255.0F;
         float blue = (tint & 0xFF) / 255.0F;
         buffer.putBulkData(matrix, quad, red, green, blue, light, overlay, true);
      }
   }
}
