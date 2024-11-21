package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.TeamTaskScoreboardEntity;
import iskallia.vault.world.data.TeamTaskData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeamTaskScoreboardRenderer extends EntityRenderer<TeamTaskScoreboardEntity> {
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/team_task_scoreboard_front.png");
   private static final ResourceLocation BACK_TEXTURE = VaultMod.id("textures/entity/team_task_scoreboard_back.png");

   public TeamTaskScoreboardRenderer(Context p_174008_) {
      super(p_174008_);
   }

   public void render(TeamTaskScoreboardEntity scoreboard, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.pushPose();
      poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
      float f = 0.0625F;
      poseStack.scale(0.0625F, 0.0625F, 0.0625F);
      this.renderBase(poseStack, buffer, scoreboard, scoreboard.getWidth(), scoreboard.getHeight());
      poseStack.popPose();
      poseStack.pushPose();
      poseStack.mulPose(scoreboard.getDirection().getRotation());
      poseStack.mulPose(Vector3f.XN.rotationDegrees(90.0F));
      this.drawStandings(poseStack, scoreboard.getLevel(), buffer, packedLight, scoreboard.getWidth(), scoreboard.getHeight());
      poseStack.popPose();
      super.render(scoreboard, entityYaw, partialTicks, poseStack, buffer, packedLight);
   }

   private void drawStandings(PoseStack poseStack, Level level, MultiBufferSource buffer, int pPackedLight, int width, int height) {
      Font font = Minecraft.getInstance().font;
      float left = -width / 2.0F / 16.0F + 0.125F;
      float innerWidth = width / 16.0F - 0.125F - 0.125F;
      float yOffset = -height / 2.0F / 16.0F + height / 16.0F - 0.125F;
      float innerHeight = height / 16.0F - 0.125F - 0.125F;
      poseStack.pushPose();
      poseStack.translate(left, yOffset, 0.0313125);
      float scale = Math.min(0.025F, width / 16.0F / 4.0F / 30.0F);
      poseStack.scale(scale, -scale, scale);
      int padding = 2;
      poseStack.translate(padding, padding, 0.0);
      int innerFontPixels = (int)(innerWidth / scale) - padding * 2;
      Matrix4f pose = poseStack.last().pose();
      int y = 0;
      font.drawInBatch(new TextComponent("Team").withStyle(ChatFormatting.GRAY), 0.0F, 0.0F, -1, false, pose, buffer, false, 0, pPackedLight);
      Component pointsLabel = new TextComponent("Points").withStyle(ChatFormatting.GRAY);
      font.drawInBatch(pointsLabel, innerFontPixels - font.width(pointsLabel) + 1, 0.0F, -1, false, pose, buffer, false, 0, pPackedLight);
      y += 9;
      fill(poseStack.last().pose(), buffer.getBuffer(RenderType.lightning()), -1, y, innerFontPixels + 1, y + 1, ChatFormatting.GRAY.getColor() | 0xFF000000);
      y += 9 / 2;
      TeamTaskData teamTaskData = TeamTaskData.get();
      int i = 0;
      Scoreboard scoreboard = level.getScoreboard();

      for (TeamTaskData.TeamScore teamScore : teamTaskData.getTeamScores()) {
         Style pointsColor = switch (i) {
            case 0 -> Style.EMPTY.withColor(16766720);
            case 1 -> Style.EMPTY.withColor(12632256);
            case 2 -> Style.EMPTY.withColor(13467442);
            default -> Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
         };
         Component points = new TextComponent(String.valueOf(teamScore.completedTasks().size())).withStyle(pointsColor);
         PlayerTeam playerTeam = scoreboard.getPlayerTeam(teamScore.teamName());
         if (playerTeam != null) {
            FormattedCharSequence team = Language.getInstance()
               .getVisualOrder(font.substrByWidth(playerTeam.getDisplayName().copy().withStyle(playerTeam.getColor()), innerFontPixels - 3 * font.width("0")));
            font.drawInBatch(team, 0.0F, y, -1, false, pose, buffer, false, 0, pPackedLight);
            font.drawInBatch(points, innerFontPixels - font.width(points) + 1, y, -1, false, pose, buffer, false, 0, pPackedLight);
            y += 9;
            i++;
            if ((y + 9) * scale > innerHeight) {
               break;
            }
         }
      }

      poseStack.popPose();
      RenderSystem.enableDepthTest();
   }

   private static void fill(Matrix4f matrix, VertexConsumer buffer, int minX, int minY, int maxX, int maxY, int color) {
      if (minX < maxX) {
         int i = minX;
         minX = maxX;
         maxX = i;
      }

      if (minY < maxY) {
         int j = minY;
         minY = maxY;
         maxY = j;
      }

      float f3 = (color >> 24 & 0xFF) / 255.0F;
      float f = (color >> 16 & 0xFF) / 255.0F;
      float f1 = (color >> 8 & 0xFF) / 255.0F;
      float f2 = (color & 0xFF) / 255.0F;
      RenderSystem.disableDepthTest();
      RenderSystem.disableTexture();
      RenderSystem.disableBlend();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      buffer.vertex(matrix, minX, maxY, 0.0F).color(f, f1, f2, f3).endVertex();
      buffer.vertex(matrix, maxX, maxY, 0.0F).color(f, f1, f2, f3).endVertex();
      buffer.vertex(matrix, maxX, minY, 0.0F).color(f, f1, f2, f3).endVertex();
      buffer.vertex(matrix, minX, minY, 0.0F).color(f, f1, f2, f3).endVertex();
      RenderSystem.enableBlend();
      RenderSystem.enableTexture();
      RenderSystem.enableDepthTest();
   }

   private void renderBase(PoseStack poseStack, MultiBufferSource buffer, TeamTaskScoreboardEntity scoreboard, int width, int height) {
      Pose posestack$pose = poseStack.last();
      Matrix4f matrix4f = posestack$pose.pose();
      Matrix3f matrix3f = posestack$pose.normal();
      float xOffset = -width / 2.0F;
      float yOffset = -height / 2.0F;
      float f3 = 0.0F;
      float f4 = 1.0F;
      float f5 = 0.0F;
      float f6 = 1.0F;
      float f7 = 0.0F;
      float f8 = 1.0F;
      float f9 = 0.0F;
      float f10 = 0.0625F;
      float f11 = 0.0F;
      float f12 = 0.0625F;
      float f13 = 0.0F;
      float f14 = 1.0F;
      int blockWidth = width / 16;
      int blockHeight = height / 16;
      VertexConsumer backVertexConsumer = buffer.getBuffer(RenderType.entitySolid(BACK_TEXTURE));

      for (int blockColumnIndex = 0; blockColumnIndex < blockWidth; blockColumnIndex++) {
         for (int blockRowIndex = 0; blockRowIndex < blockHeight; blockRowIndex++) {
            float f15 = xOffset + (blockColumnIndex + 1) * 16;
            float f16 = xOffset + blockColumnIndex * 16;
            float f17 = yOffset + (blockRowIndex + 1) * 16;
            float f18 = yOffset + blockRowIndex * 16;
            int lightColor = getLightColor(scoreboard, f17, f18, f15, f16);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f17, f4, f5, 0.5F, 0, 0, 1, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f17, f3, f5, 0.5F, 0, 0, 1, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f18, f3, f6, 0.5F, 0, 0, 1, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f18, f4, f6, 0.5F, 0, 0, 1, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f17, f7, f9, -0.5F, 0, 1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f17, f8, f9, -0.5F, 0, 1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f17, f8, f10, 0.5F, 0, 1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f17, f7, f10, 0.5F, 0, 1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f18, f7, f9, 0.5F, 0, -1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f18, f8, f9, 0.5F, 0, -1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f18, f8, f10, -0.5F, 0, -1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f18, f7, f10, -0.5F, 0, -1, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f17, f12, f13, 0.5F, -1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f18, f12, f14, 0.5F, -1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f18, f11, f14, -0.5F, -1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f15, f17, f11, f13, -0.5F, -1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f17, f12, f13, -0.5F, 1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f18, f12, f14, -0.5F, 1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f18, f11, f14, 0.5F, 1, 0, 0, lightColor);
            this.vertex(matrix4f, matrix3f, backVertexConsumer, f16, f17, f11, f13, 0.5F, 1, 0, 0, lightColor);
         }
      }

      VertexConsumer frontVertexConsumer = buffer.getBuffer(RenderType.entitySolid(this.getTextureLocation(scoreboard)));
      float singleBlockQuadHeightPartial = Math.min(blockHeight / 2.0F, 1.0F);
      float singleBlockQuadWidthPartial = Math.min(blockWidth / 2.0F, 1.0F);

      for (int blockColumnIndex = 0; blockColumnIndex < blockWidth; blockColumnIndex++) {
         for (int blockRowIndex = 0; blockRowIndex < blockHeight; blockRowIndex++) {
            boolean lastHorizontal = blockColumnIndex > 0 && blockColumnIndex == blockWidth - 1;
            boolean lastVertical = blockRowIndex > 0 && blockRowIndex == blockHeight - 1;
            float minU = lastHorizontal ? 0.75F : blockColumnIndex * 16 / 64.0F;
            float maxU = lastHorizontal ? 1.0F : (blockColumnIndex + 1) * 16 / 64.0F * singleBlockQuadWidthPartial;
            float minV = lastVertical ? 0.75F : blockRowIndex * 16 / 64.0F;
            float maxV = lastVertical ? 1.0F : (blockRowIndex + 1) * 16 / 64.0F * singleBlockQuadHeightPartial;
            this.renderFrontQuad(
               scoreboard,
               xOffset,
               yOffset,
               matrix4f,
               matrix3f,
               frontVertexConsumer,
               blockColumnIndex * 16,
               (blockColumnIndex + 1) * 16 * singleBlockQuadWidthPartial,
               blockRowIndex * 16,
               (blockRowIndex + 1) * 16 * singleBlockQuadHeightPartial,
               minU,
               maxU,
               minV,
               maxV
            );
            if (singleBlockQuadHeightPartial < 1.0F) {
               this.renderFrontQuad(
                  scoreboard,
                  xOffset,
                  yOffset,
                  matrix4f,
                  matrix3f,
                  frontVertexConsumer,
                  blockColumnIndex * 16,
                  (blockColumnIndex + 1) * 16 * singleBlockQuadWidthPartial,
                  8.0F,
                  16.0F,
                  minU,
                  maxU,
                  0.875F,
                  1.0F
               );
            }

            if (singleBlockQuadWidthPartial < 1.0F) {
               this.renderFrontQuad(
                  scoreboard,
                  xOffset,
                  yOffset,
                  matrix4f,
                  matrix3f,
                  frontVertexConsumer,
                  8.0F,
                  16.0F,
                  blockRowIndex * 16,
                  (blockRowIndex + 1) * 16 * singleBlockQuadHeightPartial,
                  0.875F,
                  1.0F,
                  minV,
                  maxV
               );
            }

            if (singleBlockQuadWidthPartial < 1.0F && singleBlockQuadHeightPartial < 1.0F) {
               this.renderFrontQuad(scoreboard, xOffset, yOffset, matrix4f, matrix3f, frontVertexConsumer, 8.0F, 16.0F, 8.0F, 16.0F, 0.875F, 1.0F, 0.875F, 1.0F);
            }
         }
      }
   }

   private void renderFrontQuad(
      TeamTaskScoreboardEntity scoreboard,
      float xOffset,
      float yOffset,
      Matrix4f matrix4f,
      Matrix3f matrix3f,
      VertexConsumer frontVertexConsumer,
      float minxXOffset,
      float maxXOffset,
      float minYOffset,
      float maxYOffset,
      float minU,
      float maxU,
      float minV,
      float maxV
   ) {
      float maxX = xOffset + maxXOffset;
      float minX = xOffset + minxXOffset;
      float maxY = yOffset + maxYOffset;
      float minY = yOffset + minYOffset;
      int lightColor = getLightColor(scoreboard, maxY, minY, maxX, minX);
      this.vertex(matrix4f, matrix3f, frontVertexConsumer, minX, minY, minU, minV, -0.5F, 0, 0, -1, lightColor);
      this.vertex(matrix4f, matrix3f, frontVertexConsumer, minX, maxY, minU, maxV, -0.5F, 0, 0, -1, lightColor);
      this.vertex(matrix4f, matrix3f, frontVertexConsumer, maxX, maxY, maxU, maxV, -0.5F, 0, 0, -1, lightColor);
      this.vertex(matrix4f, matrix3f, frontVertexConsumer, maxX, minY, maxU, minV, -0.5F, 0, 0, -1, lightColor);
   }

   private static int getLightColor(TeamTaskScoreboardEntity scoreboard, float maxY, float minY, float maxX, float minX) {
      int i1 = scoreboard.getBlockX();
      int j1 = Mth.floor(scoreboard.getY() + (maxY + minY) / 2.0F / 16.0F);
      int k1 = scoreboard.getBlockZ();
      Direction direction = scoreboard.getDirection();
      if (direction == Direction.NORTH) {
         i1 = Mth.floor(scoreboard.getX() + (maxX + minX) / 2.0F / 16.0F);
      }

      if (direction == Direction.WEST) {
         k1 = Mth.floor(scoreboard.getZ() - (maxX + minX) / 2.0F / 16.0F);
      }

      if (direction == Direction.SOUTH) {
         i1 = Mth.floor(scoreboard.getX() - (maxX + minX) / 2.0F / 16.0F);
      }

      if (direction == Direction.EAST) {
         k1 = Mth.floor(scoreboard.getZ() + (maxX + minX) / 2.0F / 16.0F);
      }

      return LevelRenderer.getLightColor(scoreboard.level, new BlockPos(i1, j1, k1));
   }

   private void vertex(
      Matrix4f matrix4f,
      Matrix3f matrix3f,
      VertexConsumer vertexConsumer,
      float p_115540_,
      float p_115541_,
      float p_115542_,
      float p_115543_,
      float p_115544_,
      int p_115545_,
      int p_115546_,
      int p_115547_,
      int p_115548_
   ) {
      vertexConsumer.vertex(matrix4f, p_115540_, p_115541_, p_115544_)
         .color(255, 255, 255, 255)
         .uv(p_115542_, p_115543_)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(p_115548_)
         .normal(matrix3f, p_115545_, p_115546_, p_115547_)
         .endVertex();
   }

   public ResourceLocation getTextureLocation(TeamTaskScoreboardEntity pEntity) {
      return TEXTURE;
   }
}
