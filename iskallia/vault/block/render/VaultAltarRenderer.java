package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class VaultAltarRenderer implements BlockEntityRenderer<VaultAltarTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();

   public VaultAltarRenderer(Context context) {
   }

   public void render(VaultAltarTileEntity altar, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      if (altar.getAltarState() != VaultAltarTileEntity.AltarState.IDLE) {
         LocalPlayer player = this.mc.player;
         int lightLevel = this.getLightAtPos(altar.getLevel(), altar.getBlockPos().above());
         this.renderItem(
            new ItemStack(ModItems.VAULT_ROCK),
            new double[]{0.5, 1.35, 0.5},
            Vector3f.YP.rotationDegrees(180.0F - player.getYRot()),
            matrixStack,
            buffer,
            partialTicks,
            combinedOverlay,
            lightLevel
         );
         if (altar.getRecipe() != null && !altar.getRecipe().getRequiredItems().isEmpty()) {
            AltarInfusionRecipe recipe = altar.getRecipe();
            List<RequiredItem> items = recipe.getRequiredItems();

            for (int i = 0; i < items.size(); i++) {
               double[] translation = this.getTranslation(i);
               RequiredItem requiredItem = items.get(i);
               ItemStack stack = requiredItem.getItem();
               TextComponent text = new TextComponent(String.valueOf(requiredItem.getAmountRequired() - requiredItem.getCurrentAmount()));
               int textColor = 16777215;
               if (requiredItem.reachedAmountRequired()) {
                  text = new TextComponent("Complete");
                  textColor = 65280;
               }

               this.renderItem(
                  stack,
                  translation,
                  Vector3f.YP.rotationDegrees(this.getAngle(player, partialTicks) * 5.0F),
                  matrixStack,
                  buffer,
                  partialTicks,
                  combinedOverlay,
                  lightLevel
               );
               this.renderLabel(requiredItem, matrixStack, buffer, lightLevel, translation, text, textColor);
            }

            if (recipe.isPogInfused()) {
               boolean infusing = altar.getAltarState() == VaultAltarTileEntity.AltarState.INFUSING;
               ItemStack pogStack = new ItemStack(ModItems.POG);
               BakedModel ibakedmodel = this.mc.getItemRenderer().getModel(pogStack, null, null, 0);

               for (int i = 0; i < 3; i++) {
                  double r = 1.0;
                  matrixStack.pushPose();
                  matrixStack.translate(0.5, 0.85, 0.5);
                  matrixStack.mulPose(Vector3f.YP.rotationDegrees(i * 120 + this.getAngle(player, partialTicks) * (infusing ? 25.0F : 15.0F)));
                  matrixStack.translate(r, Math.sin(this.getAngle(player, partialTicks) * 0.25 + i) * 0.2, 0.0);
                  matrixStack.mulPose(Vector3f.YP.rotationDegrees(this.getAngle(player, partialTicks) * 10.0F));
                  this.mc.getItemRenderer().render(pogStack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
                  matrixStack.popPose();
               }
            }
         }
      }
   }

   private void renderItem(
      ItemStack stack,
      double[] translation,
      Quaternion rotation,
      PoseStack matrixStack,
      MultiBufferSource buffer,
      float partialTicks,
      int combinedOverlay,
      int lightLevel
   ) {
      matrixStack.pushPose();
      matrixStack.translate(translation[0], translation[1], translation[2]);
      matrixStack.mulPose(rotation);
      if (stack.getItem() != ModItems.VAULT_ROCK) {
         matrixStack.scale(0.5F, 0.5F, 0.5F);
      }

      BakedModel ibakedmodel = this.mc.getItemRenderer().getModel(stack, null, null, 0);
      this.mc.getItemRenderer().render(stack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
      matrixStack.popPose();
   }

   private void renderLabel(RequiredItem item, PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, double[] corner, TextComponent text, int color) {
      Font fontRenderer = this.mc.font;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         matrixStack.pushPose();
         float scale = 0.01F;
         int opacity = 1711276032;
         float offset = -fontRenderer.width(text) / 2;
         Matrix4f matrix4f = matrixStack.last().pose();
         matrixStack.translate(corner[0], corner[1] + 0.25, corner[2]);
         matrixStack.scale(scale, scale, scale);
         matrixStack.mulPose(this.mc.getEntityRenderDispatcher().cameraOrientation());
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         fontRenderer.drawInBatch(text, offset, 0.0F, color, false, matrix4f, buffer, false, opacity, lightLevel);
         if (player.isShiftKeyDown()) {
            Component itemName = item.getItem().getHoverName();
            offset = -fontRenderer.width(itemName) / 2;
            matrixStack.translate(0.0, 1.4F, 0.0);
            matrix4f.translate(new Vector3f(0.0F, 0.15F, 0.0F));
            fontRenderer.drawInBatch(item.getItem().getHoverName(), offset, 0.0F, color, false, matrix4f, buffer, false, opacity, lightLevel);
         }

         matrixStack.popPose();
      }
   }

   private float getAngle(LocalPlayer player, float partialTicks) {
      float currentTick = player.tickCount;
      return (currentTick + partialTicks) % 360.0F;
   }

   private int getLightAtPos(Level world, BlockPos pos) {
      int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
      int skyLight = world.getBrightness(LightLayer.SKY, pos);
      return LightTexture.pack(blockLight, skyLight);
   }

   private double[] getTranslation(int index) {
      switch (index) {
         case 0:
            return new double[]{0.95, 1.35, 0.05};
         case 1:
            return new double[]{0.95, 1.35, 0.95};
         case 2:
            return new double[]{0.05, 1.35, 0.95};
         default:
            return new double[]{0.05, 1.35, 0.05};
      }
   }
}
