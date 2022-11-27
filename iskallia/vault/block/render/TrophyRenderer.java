package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.entity.TrophyTileEntity;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class TrophyRenderer implements BlockEntityRenderer<TrophyTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();

   public TrophyRenderer(Context context) {
   }

   public void render(TrophyTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      BlockState blockState = tileEntity.getBlockState();
      Direction direction = (Direction)blockState.getValue(LootStatueBlock.FACING);
      Minecraft mc = Minecraft.getInstance();
      String latestNickname = tileEntity.getSkin().getLatestNickname();
      if (!StringUtil.isNullOrEmpty(latestNickname)) {
         this.drawRecordDisplay(matrixStack, buffer, direction, tileEntity, combinedLight, combinedOverlay);
         if (mc.hitResult != null && mc.hitResult.getType() == Type.BLOCK) {
            BlockHitResult result = (BlockHitResult)mc.hitResult;
            if (tileEntity.getBlockPos().equals(result.getBlockPos())) {
               Component text = new TextComponent(latestNickname).withStyle(ChatFormatting.WHITE);
               renderLabel(mc, matrixStack, buffer, combinedLight, text, -1);
            }
         }
      }
   }

   static void renderLabel(Minecraft mc, PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, Component text, int color) {
      Font fontRenderer = mc.font;
      matrixStack.pushPose();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.width(text) / 2;
      Matrix4f matrix4f = matrixStack.last().pose();
      matrixStack.translate(0.5, 1.7F, 0.5);
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      fontRenderer.drawInBatch(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.popPose();
   }

   private void drawRecordDisplay(
      PoseStack matrixStack, MultiBufferSource buffer, Direction direction, TrophyTileEntity tileEntity, int combinedLight, int combinedOverlay
   ) {
      if (!tileEntity.isEmpty()) {
         WeekKey week = tileEntity.getWeek();
         PlayerVaultStatsData.PlayerRecordEntry recordEntry = tileEntity.getRecordEntry();
         Font fr = this.mc.font;
         LocalDateTime ldt = LocalDateTime.now();
         ldt = ldt.with(IsoFields.WEEK_BASED_YEAR, (long)week.getYear())
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, (long)week.getWeek())
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
         String from = ldt.getDayOfMonth() + "." + ldt.getMonthValue() + "." + ldt.getYear() + " -";
         ldt = ldt.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
         String to = ldt.getDayOfMonth() + "." + ldt.getMonthValue() + "." + ldt.getYear();
         FormattedCharSequence fromCmp = new TextComponent(from).getVisualOrderText();
         FormattedCharSequence toCmp = new TextComponent(to).getVisualOrderText();
         FormattedCharSequence timeStr = new TextComponent(UIHelper.formatTimeString(recordEntry.getTickCount())).getVisualOrderText();
         matrixStack.pushPose();
         matrixStack.translate(0.5, 0.5, 0.5);
         matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.24, 0.22);
         matrixStack.scale(0.0055F, -0.0055F, 0.0055F);
         int xOffset = fr.width(fromCmp);
         fr.drawInBatch(fromCmp, -xOffset / 2.0F, 0.0F, -16777216, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
         xOffset = fr.width(toCmp);
         fr.drawInBatch(toCmp, -xOffset / 2.0F, 10.0F, -16777216, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
         matrixStack.popPose();
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.1, 0.19);
         matrixStack.scale(0.008F, -0.008F, 0.008F);
         xOffset = fr.width(timeStr);
         fr.drawInBatch(timeStr, -xOffset / 2.0F, 0.0F, -16777216, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
         matrixStack.popPose();
         matrixStack.popPose();
      }
   }
}
