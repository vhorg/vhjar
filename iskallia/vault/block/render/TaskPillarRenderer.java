package iskallia.vault.block.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.TaskPillarTileEntity;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.TeamRendererContext;
import iskallia.vault.world.data.TeamTaskData;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TaskPillarRenderer implements BlockEntityRenderer<TaskPillarTileEntity> {
   public static final ResourceLocation CHECKMARK = VaultMod.id("textures/gui/bingo/checkmark.png");

   public TaskPillarRenderer(Context context) {
   }

   public void render(
      TaskPillarTileEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         TeamTaskData teamTaskData = TeamTaskData.get();
         Scoreboard scoreboard = Minecraft.getInstance().player.getScoreboard();
         RenderSystem.enableDepthTest();
         Optional<Task> reclaimTask = tileEntity.getReclaimTask(player, teamTaskData, scoreboard);
         reclaimTask.ifPresent(
            task -> this.renderTaskInfo(tileEntity, partialTicks, poseStack, packedLight, packedOverlay, task, 1.5, true, teamTaskData, scoreboard)
         );
         if (reclaimTask.isEmpty()) {
            tileEntity.getTask(player, teamTaskData, scoreboard)
               .ifPresent(
                  task -> this.renderTaskInfo(tileEntity, partialTicks, poseStack, packedLight, packedOverlay, task, 1.5, false, teamTaskData, scoreboard)
               );
         }
      }
   }

   private void renderCompletedBy(TeamRendererContext context, TaskPillarTileEntity tileEntity, TeamTaskData teamTaskData, Scoreboard scoreboard) {
      String taskId = tileEntity.getTaskId();
      if (taskId != null) {
         teamTaskData.getCompletedTaskTeam(taskId)
            .ifPresent(
               teamName -> {
                  PlayerTeam team = scoreboard.getPlayerTeam(teamName);
                  if (team != null) {
                     if (!tileEntity.hasReclaimTaskId()) {
                        context.push();
                        float scale = 2.0F;
                        context.scale(scale, scale, scale);
                        context.blit(CHECKMARK, -8, -47, 0, 0, 16, 16, 16, 16);
                        context.pop();
                     }

                     MutableComponent text = new TextComponent("Completed by ")
                        .withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD))
                        .append(team.getDisplayName().copy().withStyle(team.getColor()));
                     Font font = Minecraft.getInstance().font;
                     int width = font.width(text);
                     float offsetX = -width / 2.0F;
                     context.push();
                     context.translate(0.0, 0.0, 0.02);
                     context.fill((int)(offsetX - 3.0F), -57, width + 6, 12, 855638016);
                     context.pop();
                     context.renderText(text, 0.0F, -50.0F, 200, true, true, 16777215, false);
                  }
               }
            );
      }
   }

   private void renderTaskInfo(
      TaskPillarTileEntity tileEntity,
      float partialTicks,
      PoseStack poseStack,
      int packedLight,
      int packedOverlay,
      Task task,
      double yOffset,
      boolean showToClaim,
      TeamTaskData teamTaskData,
      Scoreboard scoreboard
   ) {
      poseStack.pushPose();
      float scale = 0.012F;
      poseStack.translate(0.5, yOffset, 0.5);
      poseStack.scale(scale, scale, scale);
      poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      TeamRendererContext context = TeamRendererContext.forWorld(poseStack, partialTicks, Minecraft.getInstance().font, packedLight, packedOverlay);
      this.renderCompletedBy(context, tileEntity, teamTaskData, scoreboard);
      if (showToClaim) {
         MutableComponent claimText = new TextComponent("To Claim").withStyle(ChatFormatting.GREEN);
         int width = Minecraft.getInstance().font.width(claimText);
         int halfWidth = width / 2;
         int y = -35;
         context.fill(-halfWidth - 3, y - 6, width + 6, 10, 855638016);
         context.push();
         context.translate(0.0, 0.0, -0.1);
         context.renderText(claimText, 0.0F, y, 200, true, true, 16777215, false);
         context.pop();
      }

      task.<Task, TeamRendererContext>getRenderer().onRender(task, context);
      poseStack.popPose();
   }
}
