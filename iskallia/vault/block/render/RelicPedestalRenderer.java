package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.block.RelicPedestalBlock;
import iskallia.vault.block.entity.RelicPedestalTileEntity;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRelics;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RelicPedestalRenderer implements BlockEntityRenderer<RelicPedestalTileEntity> {
   public RelicPedestalRenderer(Context context) {
   }

   public void render(
      RelicPedestalTileEntity statue,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = statue.getLevel();
      BlockPos blockPos = statue.getBlockPos();
      if (world != null) {
         BlockState blockState = world.getBlockState(blockPos);
         Block block = blockState.getBlock();
         if (block instanceof RelicPedestalBlock) {
            ModRelics.RelicRecipe relicRecipe = (ModRelics.RelicRecipe)blockState.getValue(RelicPedestalBlock.RELIC);
            if (relicRecipe != ModRelics.EMPTY) {
               ItemStack itemStack = new ItemStack(ModItems.RELIC);
               DynamicModelItem.setGenericModelId(itemStack, relicRecipe.getResultingRelic());
               matrixStack.pushPose();
               this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.5F, 1.5F, itemStack);
               matrixStack.popPose();
            }
         }
      }
   }

   private void renderItem(PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, int overlay, float yOffset, float scale, ItemStack itemStack) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      double rotation = 20.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float)rotation, 0.0F));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }
}
