package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.BoosterPackItem;
import iskallia.vault.network.message.BoosterPackSelectionMessage;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public class BoosterPackSelectionScreen extends Screen {
   private final List<ItemStack> outcomes;

   public BoosterPackSelectionScreen(ItemStack stack) {
      super(new TextComponent("Booster Pack Selection"));
      this.outcomes = BoosterPackItem.getOutcomes(stack);
   }

   protected void init() {
      super.init();
      float width = 56.0F;
      float spanX = width * this.outcomes.size();

      for (int i = 0; i < this.outcomes.size(); i++) {
         ItemStack outcome = this.outcomes.get(i);
         float x = this.width / 2.0F - spanX / 2.0F + i * width;
         this.addRenderableWidget(new BoosterPackSelectionScreen.CardSection(i, outcome, (int)x, 0, (int)width, this.height));
      }
   }

   public void render(PoseStack matrixStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, pMouseX, pMouseY, pPartialTick);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public class CardSection extends Button {
      private final int index;
      private final ItemStack stack;

      public CardSection(int index, ItemStack stack, int x, int y, int width, int height) {
         super(x, y, width, height, new TextComponent(""), Button::onPress);
         this.index = index;
         this.stack = stack;
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

         float scale = 4.0F;
         matrixStack.pushPose();
         matrixStack.translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F, 0.0);
         matrixStack.scale(scale, scale, 1.0F);
         matrixStack.translate(-8.0, -8.0, 0.0);
         PoseStack posestack = RenderSystem.getModelViewStack();
         posestack.pushPose();
         posestack.mulPoseMatrix(matrixStack.last().pose());
         RenderSystem.applyModelViewMatrix();
         Minecraft.getInstance().getItemRenderer().renderGuiItem(this.stack, 0, 0);
         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
         matrixStack.popPose();
         if (mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height) {
            BoosterPackSelectionScreen.this.renderTooltip(matrixStack, this.stack, mouseX, mouseY);
         }
      }

      public void onPress() {
         ModNetwork.CHANNEL.sendToServer(new BoosterPackSelectionMessage(this.index));
         BoosterPackSelectionScreen.this.onClose();
      }
   }
}
