package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.SkillAltarBlock;
import iskallia.vault.block.entity.SkillAltarTileEntity;
import iskallia.vault.client.ClientSkillAltarData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.world.data.SkillAltarData;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class SkillAltarRenderer implements BlockEntityRenderer<SkillAltarTileEntity> {
   public static final String TALENT_ICON_PREFIX = "talent/";
   public static final String ABILITY_ICON_PREFIX = "ability/";
   private static final int MIN_ICON_SWITCH_TIME = 5000;
   private static final int MAX_ICON_SWITCH_TIME = 10000;
   private static final int EASING_TIME = 600;
   private static final int MAX_ALPHA = 208;

   public SkillAltarRenderer(Context context) {
   }

   public void render(
      SkillAltarTileEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      if (blockEntity.getLevel() != null) {
         List<SkillAltarData.SkillIcon> skillAltarIconKeys = ClientSkillAltarData.getAbilityIconKeys(blockEntity.getOwnerId());
         float alphaMultiplier = 1.0F;
         int iconKeyIndex = blockEntity.getRenderIconKeyIndex();
         if (iconKeyIndex < 0 && !skillAltarIconKeys.isEmpty()) {
            iconKeyIndex = 0;
            blockEntity.switchToNextIcon(
               iconKeyIndex, System.currentTimeMillis(), System.currentTimeMillis() + blockEntity.getLevel().random.nextInt(5000, 10000)
            );
         }

         if (skillAltarIconKeys.size() > 1) {
            long lastIconSwitchTime = blockEntity.getLastIconSwitchTime();
            long nextIconSwitchTime = blockEntity.getNextIconSwitchTime();
            if (System.currentTimeMillis() >= nextIconSwitchTime) {
               iconKeyIndex = (iconKeyIndex + 1) % skillAltarIconKeys.size();
               lastIconSwitchTime = System.currentTimeMillis();
               nextIconSwitchTime = System.currentTimeMillis() + blockEntity.getLevel().random.nextInt(5000, 10000);
               blockEntity.switchToNextIcon(iconKeyIndex, lastIconSwitchTime, nextIconSwitchTime);
            }

            if (System.currentTimeMillis() - lastIconSwitchTime < 600L) {
               int sinceLastSwitch = (int)(System.currentTimeMillis() - lastIconSwitchTime);
               float progress = sinceLastSwitch / 600.0F;
               alphaMultiplier = Easing.EASE_OUT_BOUNCE.calc(progress);
            } else if (nextIconSwitchTime - System.currentTimeMillis() < 600L) {
               int sinceNextSwitch = (int)(nextIconSwitchTime - System.currentTimeMillis());
               float progress = sinceNextSwitch / 600.0F;
               alphaMultiplier = Easing.EASE_OUT_BOUNCE.calc(progress);
            }
         }

         this.renderIcon(blockEntity, poseStack, bufferSource, packedLight, skillAltarIconKeys, alphaMultiplier, iconKeyIndex);
      }
   }

   private void renderIcon(
      SkillAltarTileEntity blockEntity,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      List<SkillAltarData.SkillIcon> skillAltarIconKeys,
      float alphaMultiplier,
      int iconKeyIndex
   ) {
      ResourceLocation atlasResourceLocation;
      TextureAtlasSprite icon;
      if (skillAltarIconKeys.isEmpty()
         || iconKeyIndex < 0
         || iconKeyIndex >= skillAltarIconKeys.size()
         || skillAltarIconKeys.get(iconKeyIndex).key().equals("")) {
         atlasResourceLocation = ScreenTextures.TAB_ICON_ABILITIES.atlas().get().getAtlasResourceLocation();
         icon = ScreenTextures.TAB_ICON_ABILITIES.getSprite();
      } else if (skillAltarIconKeys.get(iconKeyIndex).isTalent()) {
         atlasResourceLocation = ModTextureAtlases.SKILLS.get().getAtlasResourceLocation();
         SkillStyle style = ModConfigs.TALENTS_GUI.getStyles().get(skillAltarIconKeys.get(iconKeyIndex).key());
         if (style != null) {
            icon = ModTextureAtlases.SKILLS.get().getSprite(style.icon);
         } else {
            icon = ScreenTextures.TAB_ICON_ABILITIES.getSprite();
         }
      } else {
         atlasResourceLocation = ModTextureAtlases.ABILITIES.get().getAtlasResourceLocation();
         ResourceLocation style = ModConfigs.ABILITIES_GUI.getIcon(skillAltarIconKeys.get(iconKeyIndex).key());
         if (style != null) {
            icon = ModTextureAtlases.ABILITIES.get().getSprite(style);
         } else {
            icon = ScreenTextures.TAB_ICON_ABILITIES.getSprite();
         }
      }

      poseStack.pushPose();
      poseStack.translate(0.5, 0.925, 0.5);
      poseStack.mulPose(Vector3f.YN.rotationDegrees(((Direction)blockEntity.getBlockState().getValue(SkillAltarBlock.FACING)).getOpposite().toYRot()));
      poseStack.translate(0.25, 0.0, 0.0);
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      poseStack.scale(0.5F, 0.5F, 0.5F);
      VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(atlasResourceLocation));
      Pose pose = poseStack.last();
      Matrix4f matrix4f = pose.pose();
      Matrix3f normal = pose.normal();
      float f15 = 1.0F;
      float f16 = 0.0F;
      float f17 = 1.0F;
      float f18 = 0.0F;
      float uMin = icon.getU0();
      float uMax = icon.getU1();
      float vMin = icon.getV0();
      float vMax = icon.getV1();
      int alpha = (int)(alphaMultiplier * 208.0F);
      this.vertex(matrix4f, normal, vertexconsumer, f15, f18, uMax, vMin, 0.0F, 0, 0, 0, packedLight, alpha);
      this.vertex(matrix4f, normal, vertexconsumer, f16, f18, uMin, vMin, 0.0F, 0, 0, 0, packedLight, alpha);
      this.vertex(matrix4f, normal, vertexconsumer, f16, f17, uMin, vMax, 0.0F, 0, 0, 0, packedLight, alpha);
      this.vertex(matrix4f, normal, vertexconsumer, f15, f17, uMax, vMax, 0.0F, 0, 0, 0, packedLight, alpha);
      if (bufferSource instanceof BufferSource bs) {
         bs.endBatch();
      }

      poseStack.popPose();
   }

   private void vertex(
      Matrix4f matrix4f,
      Matrix3f normal,
      VertexConsumer vertexConsumer,
      float x,
      float y,
      float u,
      float v,
      float z,
      int normalX,
      int normalY,
      int normalZ,
      int light,
      int alpha
   ) {
      vertexConsumer.vertex(matrix4f, x, y, z)
         .color(255, 255, 255, alpha)
         .uv(u, v)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(light)
         .normal(normal, normalX, normalY, normalZ)
         .endVertex();
   }
}
