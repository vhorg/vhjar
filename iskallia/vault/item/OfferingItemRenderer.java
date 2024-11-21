package iskallia.vault.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class OfferingItemRenderer extends SpecialItemRenderer {
   public static final OfferingItemRenderer INSTANCE = new OfferingItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      String modifier = OfferingItem.getModifier(stack);
      ModelResourceLocation base = new ModelResourceLocation("the_vault:offering/%s#inventory".formatted(modifier));
      this.renderModel(base, 16777215, stack, transformType, poseStack, buffer, light, overlay, null);
      List<ItemStack> items = OfferingItem.getItems(stack);
      if (!items.isEmpty()) {
         ItemStack lootItem = items.get(0);
         ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
         BakedModel model = itemRenderer.getModel(lootItem, null, null, 0);
         if (transformType == TransformType.GUI && !model.usesBlockLight()) {
            Lighting.setupForFlatItems();
         }

         poseStack.pushPose();
         translate(poseStack, modifier);
         poseStack.scale(0.5F, 0.5F, 1.0F);
         itemRenderer.render(lootItem, transformType, false, poseStack, buffer, light, overlay, model);
         if (buffer instanceof BufferSource src) {
            RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
            src.endBatch(renderType);
         }

         if (transformType == TransformType.GUI && !model.usesBlockLight()) {
            Lighting.setupFor3DItems();
         }

         poseStack.popPose();
      }
   }

   public static void translate(PoseStack matrices, String modifier) {
      if (modifier.equals("light_range_attack")) {
         matrices.translate(0.5, 0.53F, 0.51F);
      } else if (modifier.equals("light_melee_attack")) {
         matrices.translate(0.5, 0.73F, 0.51F);
      }
   }
}
