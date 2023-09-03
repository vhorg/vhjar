package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.AlchemyTableTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class AlchemyTableRenderer implements BlockEntityRenderer<AlchemyTableTileEntity> {
   public AlchemyTableRenderer(Context context) {
   }

   public void render(
      AlchemyTableTileEntity table,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = table.getLevel();
      if (world != null) {
         ItemStack itemStack = table.getInventory().getItem(0).copy();
         int craftingOutputCooldown = table.getCraftingOutputCooldown();
         if (table.isCrafting() && craftingOutputCooldown > 20) {
            itemStack = table.getFakeItemStack();
         }

         matrixStack.pushPose();
         this.renderPotion(matrixStack, buffer, combinedLight, combinedOverlay, 0.725F, 0.45F, itemStack, table, partialTicks, craftingOutputCooldown);
         matrixStack.popPose();
      }
   }

   private void renderPotion(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      int lightLevel,
      int overlay,
      float yOffset,
      float scale,
      ItemStack itemStack,
      AlchemyTableTileEntity table,
      float partialTicks,
      int craftingOutputCooldown
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      float extraDegrees = 0.0F;
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      if (craftingOutputCooldown > 0 && craftingOutputCooldown < AlchemyTableTileEntity.CRAFTING_COOLDOWN - 10) {
         float f = 1.0F - (float)craftingOutputCooldown / (AlchemyTableTileEntity.CRAFTING_COOLDOWN - 10);
         f = AlchemyTableTileEntity.ease(f);
         matrixStack.translate(0.0, Math.sin(f * Math.PI / 1.5) / 8.0, 0.0);
      }

      if (craftingOutputCooldown > 0) {
         extraDegrees = Mth.lerp(partialTicks, table.getExtraSpinDegreesPrev(), table.getExtraSpinDegrees());
      }

      matrixStack.scale(scale, scale, scale);
      Direction facingDirection = (Direction)table.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
      int degrees = facingDirection != Direction.NORTH && facingDirection != Direction.SOUTH ? 0 : 90;
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(degrees + 45 + extraDegrees));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }
}
