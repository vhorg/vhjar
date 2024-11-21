package iskallia.vault.block.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.challenge.elite.EliteControllerProxyBlockEntity;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class EliteControllerProxyRenderer implements BlockEntityRenderer<EliteControllerProxyBlockEntity> {
   private final Font font;

   public EliteControllerProxyRenderer(Context context) {
      this.font = context.getFont();
   }

   public void render(
      EliteControllerProxyBlockEntity entity, float partialTick, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay
   ) {
      RenderSystem.enableDepthTest();
      matrixStack.pushPose();
      float scale = 0.02F;
      matrixStack.translate(0.5, 2.2, 0.5);
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      matrixStack.translate(-65.0, -11.0, 0.0);
      RendererContext context = new RendererContext(matrixStack, partialTick, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), this.font);
      List<Component> lines = new ArrayList<>();

      for (ChallengeAction<?> action : entity.getActions()) {
         lines.add(action.getText());
      }

      Collections.reverse(lines);

      for (Component line : lines) {
         Component shadow = new TextComponent("").append(line.getString()).withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK));
         context.renderText(shadow, 66.0F, 66.0F, true, true);
         context.renderText(line, 65.0F, 65.0F, true, true);
         context.translate(0.0, -11.0, 0.0);
      }

      matrixStack.popPose();
   }
}
