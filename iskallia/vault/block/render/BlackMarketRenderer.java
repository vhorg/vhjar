package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.BlackMarketBlock;
import iskallia.vault.block.entity.BlackMarketTileEntity;
import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class BlackMarketRenderer implements BlockEntityRenderer<BlackMarketTileEntity> {
   public BlackMarketRenderer(Context context) {
   }

   public void render(
      BlackMarketTileEntity blackMarketTile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = blackMarketTile.getLevel();
      if (world != null) {
         Direction dir = (Direction)blackMarketTile.getBlockState().getValue(BlackMarketBlock.FACING);
         if (ClientShardTradeData.getAvailableTrades().containsKey(1)) {
            ItemStack itemStack = (ItemStack)ClientShardTradeData.getTradeInfo(1).getA();
            matrixStack.pushPose();
            this.renderInputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, itemStack, dir, 0);
            matrixStack.popPose();
         }

         if (ClientShardTradeData.getAvailableTrades().containsKey(0)) {
            ItemStack itemStack = (ItemStack)ClientShardTradeData.getTradeInfo(0).getA();
            matrixStack.pushPose();
            this.renderInputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, itemStack, dir, 1);
            matrixStack.popPose();
         }

         if (ClientShardTradeData.getAvailableTrades().containsKey(2)) {
            ItemStack itemStack = (ItemStack)ClientShardTradeData.getTradeInfo(2).getA();
            matrixStack.pushPose();
            this.renderInputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, itemStack, dir, 2);
            matrixStack.popPose();
         }

         matrixStack.pushPose();
         this.renderOutputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, new ItemStack(ModItems.SOUL_SHARD), dir, 0);
         matrixStack.popPose();
         matrixStack.pushPose();
         this.renderOutputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, new ItemStack(ModItems.SOUL_SHARD), dir, 1);
         matrixStack.popPose();
         matrixStack.pushPose();
         this.renderOutputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, new ItemStack(ModItems.SOUL_SHARD), dir, 2);
         matrixStack.popPose();
         matrixStack.pushPose();
         this.renderOutputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, new ItemStack(ModItems.SOUL_SHARD), dir, 3);
         matrixStack.popPose();
         matrixStack.pushPose();
         this.renderOutputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.64F, 0.35F, new ItemStack(ModItems.SOUL_SHARD), dir, 4);
         matrixStack.popPose();
      }
   }

   private void renderInputItem(
      PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, int overlay, float yOffset, float scale, ItemStack itemStack, Direction dir, int i
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      boolean is3d = bakedModel.isGui3d();
      Block itemBlock = (Block)ForgeRegistries.BLOCKS.getValue(itemStack.getItem().getRegistryName());
      boolean shouldLower = is3d && (itemBlock == null || itemBlock == Blocks.AIR);
      int rot = 0;
      if (dir == Direction.WEST) {
         rot = 90;
      }

      if (dir == Direction.SOUTH) {
         rot = 180;
      }

      if (dir == Direction.EAST) {
         rot = 270;
      }

      matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      matrixStack.translate(
         i == 0 ? 0.0 : (i == 1 ? 0.8 : -0.8), 0.7 + (i == 0 ? 0.0 : -0.05), (i == 0 ? -0.01 : 0.0) - (is3d ? (shouldLower ? 0.05 : 0.2) : 0.0)
      );
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(i == 0 ? 0.0F : (i == 1 ? -30.0F : 30.0F)));
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }

   private void renderOutputItem(
      PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, int overlay, float yOffset, float scale, ItemStack itemStack, Direction dir, int i
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      int rot = 0;
      if (dir == Direction.WEST) {
         rot = 90;
      }

      if (dir == Direction.SOUTH) {
         rot = 180;
      }

      if (dir == Direction.EAST) {
         rot = 270;
      }

      matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      matrixStack.translate(
         i == 0 ? 0.0 : (i == 1 ? 0.5 : (i == 2 ? -0.5 : (i == 3 ? -0.9 : 0.9))), -0.7 + (i == 0 ? 0.1 : 0.0), 0.0 + (i != 1 && i != 2 ? 0.0 : -0.05)
      );
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(i == 0 ? 0.0F : (i == 1 ? -50.0F : (i == 2 ? 100.0F : (i == 3 ? 0.0F : 100.0F)))));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }
}
