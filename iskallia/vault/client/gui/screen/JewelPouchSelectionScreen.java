package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.network.message.JewelPouchSelectionMessage;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public class JewelPouchSelectionScreen extends Screen {
   private final List<JewelPouchItem.RolledJewel> jewels;

   public JewelPouchSelectionScreen(ItemStack stack) {
      super(new TextComponent("Jewel Box Selection"));
      this.jewels = JewelPouchItem.getJewels(stack);
   }

   protected void init() {
      super.init();
      float width = 56.0F;
      float spanX = width * this.jewels.size();

      for (int i = 0; i < this.jewels.size(); i++) {
         JewelPouchItem.RolledJewel outcome = this.jewels.get(i);
         float x = this.width / 2.0F - spanX / 2.0F + i * width;
         this.addRenderableWidget(new JewelPouchSelectionScreen.JewelSelection(i, outcome, (int)x, 0, (int)width, this.height));
      }
   }

   public void render(PoseStack matrixStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, pMouseX, pMouseY, pPartialTick);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public class JewelSelection extends Button {
      private final int index;
      private final JewelPouchItem.RolledJewel outcome;

      public JewelSelection(int index, JewelPouchItem.RolledJewel outcome, int x, int y, int width, int height) {
         super(x, y, width, height, new TextComponent(""), Button::onPress);
         this.index = index;
         this.outcome = outcome;
      }

      public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
         if (this.isHoveredOrFocused()) {
            RenderSystem.enableBlend();
            ScreenDrawHelper.draw(
               Mode.QUADS,
               DefaultVertexFormat.POSITION_COLOR,
               buf -> ScreenDrawHelper.rect(buf, matrixStack).at(this.x, this.y).dim(this.width, this.height).color(583847116).drawColored()
            );
         }

         ItemStack renderStack = this.outcome.stack();
         float scale = 4.0F;
         matrixStack.pushPose();
         matrixStack.translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F, 0.0);
         matrixStack.scale(scale, scale, 1.0F);
         matrixStack.translate(-8.0, -8.0, 0.0);
         PoseStack posestack = RenderSystem.getModelViewStack();
         posestack.pushPose();
         posestack.mulPoseMatrix(matrixStack.last().pose());
         RenderSystem.applyModelViewMatrix();
         ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
         renderer.renderGuiItem(renderStack, 0, 0);
         renderer.renderGuiItemDecorations(Minecraft.getInstance().font, renderStack, 0, 0, "");
         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
         matrixStack.popPose();
         if (mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height) {
            List<Component> tooltip = JewelPouchSelectionScreen.this.getTooltipFromItem(renderStack);
            JewelPouchSelectionScreen.this.renderTooltip(matrixStack, tooltip, Optional.empty(), mouseX, mouseY);
         }
      }

      public void onPress() {
         ModNetwork.CHANNEL.sendToServer(new JewelPouchSelectionMessage(this.index));
         JewelPouchSelectionScreen.this.onClose();
      }
   }
}
