package iskallia.vault.item.tool;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.datafixers.util.Pair;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ToolItemRenderer extends SpecialItemRenderer {
   public static final ToolItemRenderer INSTANCE = new ToolItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      ToolType type = ToolType.of(stack);
      if (type != null) {
         VaultGearData data = VaultGearData.read(stack);
         ToolMaterial material = data.get(ModGearAttributes.TOOL_MATERIAL, VaultGearAttributeTypeMerger.of(() -> null, (a, b) -> b));
         if (material != null) {
            ModelResourceLocation head = new ModelResourceLocation("the_vault:tool/%s/head/%s#inventory".formatted(type.getId(), material.getId()));
            ModelResourceLocation handle = new ModelResourceLocation("the_vault:tool/%s/handle#inventory".formatted(type.getId()));
            this.renderModel(handle, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
            this.renderModel(head, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         }
      }
   }

   private void renderModel(
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
   }

   @Override
   public void renderModelLists(BakedModel model, int tint, PoseStack matrices, VertexConsumer buffer, int light, int overlay) {
      Random random = new Random();

      for (Direction direction : Direction.values()) {
         this.renderQuadList(model.getQuads(null, direction, random), tint, matrices, buffer, light, overlay);
      }

      this.renderQuadList(model.getQuads(null, null, random), tint, matrices, buffer, light, overlay);
   }

   @Override
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
