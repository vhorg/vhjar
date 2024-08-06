package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.OfferingPillarTileEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class OfferingPillarRenderer implements BlockEntityRenderer<OfferingPillarTileEntity> {
   private static final Minecraft mc = Minecraft.getInstance();
   private final EntityRenderDispatcher entityRendererDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

   public OfferingPillarRenderer(Context context) {
   }

   public void render(
      OfferingPillarTileEntity tile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = tile.getLevel();
      if (world != null) {
         this.renderBoss(tile, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
         this.renderOfferings(tile, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
         this.renderLootItems(tile, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
         this.renderModifiers(tile, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
         this.renderConsumedItem(tile, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
      }
   }

   private void renderModifiers(
      OfferingPillarTileEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      poseStack.pushPose();
      poseStack.translate(0.5, 0.0, 0.5);
      Map<String, Integer> modifiers = tile.getModifiers();
      int i = 0;

      for (Entry<String, Integer> modifier : modifiers.entrySet()) {
         String modifierName = StringUtils.convertToTitleCase(modifier.getKey());
         String text = modifier.getValue() > 1 ? modifier.getValue() + "x " + modifierName : modifierName;
         this.renderText(poseStack, buffer, combinedLight, text, 0.0F, 3.0F + i * 0.2F, 0.0F, 16777215);
         i++;
      }

      poseStack.popPose();
   }

   private void renderText(PoseStack poseStack, MultiBufferSource buffer, int lightLevel, String text, float x, float y, float z, int color) {
      poseStack.pushPose();
      float scale = 0.02F;
      Font fontRenderer = mc.font;
      float offset = -fontRenderer.width(text) / 2;
      Matrix4f matrix4f = poseStack.last().pose();
      poseStack.translate(x, y, z);
      poseStack.scale(scale, scale, scale);
      poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, buffer, true, 0, lightLevel);
      poseStack.popPose();
   }

   private void renderLootItems(
      OfferingPillarTileEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      List<ItemStack> items = tile.getItems();
      matrixStack.pushPose();
      matrixStack.translate(0.5, 1.5, 0.5);
      matrixStack.scale(0.5F, 0.5F, 0.5F);
      renderFloatingItems(
         matrixStack,
         buffer,
         combinedLight,
         combinedOverlay,
         items.size(),
         items::get,
         i -> (float)i / items.size() * 2.0F * Math.PI + Math.PI * (System.currentTimeMillis() / 3000.0) % (Math.PI * 2)
      );
      matrixStack.popPose();
   }

   private void renderOfferings(
      OfferingPillarTileEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      int required = tile.getNumberOfOfferingsRequired();
      int provided = tile.getNumberOfOfferingsProvided();
      poseStack.pushPose();
      poseStack.translate(0.5, 1.0, 0.5);
      poseStack.scale(1.0F, 1.0F, 1.0F);
      renderFloatingItems(
         poseStack,
         buffer,
         combinedLight,
         combinedOverlay,
         required,
         i -> i < provided ? new ItemStack(ModItems.OFFERING) : new ItemStack(ModItems.OFFERING_TEMPLATE),
         i -> (float)i / required * 2.0F * Math.PI + Math.PI * (System.currentTimeMillis() / 5000.0) % (Math.PI * 2)
      );
      poseStack.popPose();
   }

   private static void renderFloatingItems(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay,
      int count,
      IntFunction<ItemStack> getItem,
      IntFunction<Double> getAngle
   ) {
      for (int i = 0; i < count; i++) {
         poseStack.pushPose();
         ItemStack item = getItem.apply(i);
         if (!item.isEmpty()) {
            double angle = getAngle.apply(i);
            double y = Math.sin(angle) * 0.75;
            double x = Math.cos(angle) * 0.75;
            poseStack.translate(x, 0.2 + Math.sin(angle * 2.0) * 0.1, y);
            poseStack.mulPose(Vector3f.YN.rotation((float)(angle + (Math.PI / 2))));
            BakedModel ibakedmodel = mc.getItemRenderer().getModel(item, null, null, 0);
            mc.getItemRenderer().render(item, TransformType.GROUND, true, poseStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
            poseStack.popPose();
         }
      }
   }

   private void renderBoss(
      OfferingPillarTileEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      ResourceLocation bossId = tile.getBossId();
      EntityType<?> bossType = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(bossId);
      Entity boss = bossType.create(tile.getLevel());
      if (boss != null) {
         EntityRenderer<? super Entity> entityRenderer = this.entityRendererDispatcher.getRenderer(boss);
         poseStack.pushPose();
         poseStack.translate(0.5, 1.5, 0.5);
         poseStack.mulPose(Vector3f.YP.rotation((float)(Math.PI * (System.currentTimeMillis() / 10000.0) % (Math.PI * 2))));
         poseStack.scale(0.25F, 0.25F, 0.25F);
         entityRenderer.render(boss, 0.0F, 0.0F, poseStack, buffer, combinedLight);
         poseStack.popPose();
      }
   }

   private void renderConsumedItem(
      OfferingPillarTileEntity tile,
      float partialTicks,
      @NotNull PoseStack matrixStack,
      @NotNull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      if (!tile.getHeldItem().isEmpty()) {
         float lerp = Mth.lerp(partialTicks, tile.ticksToConsumeOld, tile.ticksToConsume);
         float percentConsumed = 1.0F - lerp / 20.0F;
         ItemStack itemStack = tile.getHeldItem();
         matrixStack.pushPose();
         this.renderItem(
            matrixStack,
            buffer,
            combinedLight,
            combinedOverlay,
            1.5F - percentConsumed * percentConsumed * percentConsumed,
            (1.0F - percentConsumed * percentConsumed) * 0.4F + 0.1F,
            itemStack,
            tile,
            partialTicks
         );
         matrixStack.popPose();
      }
   }

   private void renderItem(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      int lightLevel,
      int overlay,
      float yOffset,
      float scale,
      ItemStack itemStack,
      OfferingPillarTileEntity scavengerAltarTileEntity,
      float partialTicks
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      float lerp = Mth.lerp(partialTicks, scavengerAltarTileEntity.ticksToConsumeOld, scavengerAltarTileEntity.ticksToConsume);
      float ticksToConsumedAnimated = (20.0F - lerp) * (20.0F - lerp);
      double rotation = -10.0 * (System.currentTimeMillis() / 1000.0 + ticksToConsumedAnimated / 25.0F) % 360.0 * (Math.PI / 180.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float)rotation, 0.0F));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }
}
