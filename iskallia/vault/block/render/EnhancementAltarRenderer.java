package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.vault.enhancement.EnhancementTask;
import iskallia.vault.util.TextComponentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

public class EnhancementAltarRenderer implements BlockEntityRenderer<VaultEnhancementAltarTileEntity> {
   public EnhancementAltarRenderer(Context context) {
   }

   public void render(VaultEnhancementAltarTileEntity tile, float pTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         EnhancementTask<?> task = tile.getTasks().get(player.getUUID());
         if (task != null) {
            Component display;
            if (task.isFinished()) {
               display = new TextComponent("Completed");
            } else {
               display = task.getDisplay(TextComponentUtils.createClientSourceStack());
            }

            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            if (display != null) {
               FormattedCharSequence seq = display.getVisualOrderText();
               float offset = -fontRenderer.width(seq) / 2.0F;
               float scale = 0.025F;
               poseStack.pushPose();
               poseStack.translate(0.5, 1.7F, 0.5);
               poseStack.scale(scale, scale, scale);
               poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
               poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
               Matrix4f pose = poseStack.last().pose();
               fontRenderer.drawInBatch(seq, offset, 0.0F, -1, false, pose, buffers, false, 127, 127);
               poseStack.popPose();
            }
         }
      }
   }
}
