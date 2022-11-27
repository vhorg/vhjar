package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.helper.ConfettiParticles;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.widget.RaffleEntry;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.RaffleServerMessage;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class RaffleScreen extends Screen {
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/raffle.png");
   public static final int containerWidth = 62;
   public static final int containerHeight = 20;
   protected ConfettiParticles leftConfettiPopper = new ConfettiParticles()
      .angleRange(290.0F, 355.0F)
      .quantityRange(60, 80)
      .delayRange(0, 10)
      .lifespanRange(20, 100)
      .sizeRange(2, 5)
      .speedRange(2.0F, 10.0F);
   protected ConfettiParticles rightConfettiPopper = new ConfettiParticles()
      .angleRange(200.0F, 265.0F)
      .quantityRange(60, 80)
      .delayRange(0, 10)
      .lifespanRange(20, 100)
      .sizeRange(2, 5)
      .speedRange(2.0F, 10.0F);
   protected boolean popped;
   protected boolean spinning;
   protected List<String> occupants;
   protected String winner;
   protected List<RaffleEntry> raffleWidgets = new LinkedList<>();
   protected Button raffleButton;
   protected int spinTicks;
   protected int distance;
   protected double elapsedTicks;
   private double C;

   public RaffleScreen(List<String> occupants, String winner) {
      super(new TextComponent("Raffle Screen"));
      this.occupants = new LinkedList<>();
      this.occupants.addAll(occupants);
      Collections.shuffle(occupants);
      this.winner = winner;
      int winnerIndex = occupants.indexOf(winner);
      this.spinTicks = 200;
      int freeSpinCount = 5;
      this.distance = freeSpinCount * (occupants.size() * 20 + (occupants.size() - 1) + 1) + (winnerIndex - 2) * 21;

      for (int i = 0; i < this.occupants.size(); i++) {
         RaffleEntry entry = new RaffleEntry(occupants.get(i), i % 5);
         entry.setBounds(new Rectangle(0, i * 21, 62, 20));
         this.raffleWidgets.add(entry);
      }

      this.C = 0.5 * this.spinTicks * this.spinTicks;
   }

   protected void init() {
      super.init();
      int midX = this.width / 2;
      int midY = this.height / 2;
      this.raffleButton = new Button(midX - 50, midY + 40, 100, 20, new TextComponent("Raffle!"), this::onRaffleButtonClick);
   }

   public Rectangle getViewportBounds() {
      int midX = this.width / 2;
      int midY = this.height / 2;
      return new Rectangle(midX - 32, midY - 60, 62, 90);
   }

   public double calculateYOffset(double tick) {
      return (-(tick * tick * tick) / 6.0 + this.C * tick)
         * this.distance
         / (-(this.spinTicks * this.spinTicks * this.spinTicks) / 6.0 + this.C * this.spinTicks);
   }

   public void mouseMoved(double mouseX, double mouseY) {
      this.raffleButton.mouseMoved(mouseX, mouseY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.raffleButton.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.raffleButton.mouseReleased(mouseX, mouseY, button);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      return this.raffleButton.mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }

   public void onRaffleButtonClick(Button button) {
      this.spinning = true;
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack, 0);
      RenderSystem.setShaderTexture(0, UI_RESOURCE);
      int midX = this.width / 2;
      int midY = this.height / 2;
      int crystalWidth = 114;
      int crystalHeight = 130;
      matrixStack.pushPose();
      matrixStack.translate(midX, midY, 0.0);
      matrixStack.scale(1.5F, 1.5F, 1.5F);
      this.blit(matrixStack, -crystalWidth / 2, -crystalHeight / 2, 0, 0, crystalWidth, crystalHeight);
      matrixStack.popPose();
      UIHelper.renderOverflowHidden(matrixStack, this::renderWheelBackground, this::renderWheel);
      Rectangle viewportBounds = this.getViewportBounds();
      RenderSystem.enableBlend();
      UIHelper.renderContainerBorder(this, matrixStack, viewportBounds, 115, 0, 7, 7, 7, 7, 0);
      int indicatorWidth = 17;
      int indicatorHeight = 15;
      this.blit(matrixStack, midX - 42, (int)(viewportBounds.y + viewportBounds.height / 2.0F - indicatorHeight / 2), 115, 18, indicatorWidth, indicatorHeight);
      this.raffleButton.render(matrixStack, mouseX, mouseY, partialTicks);
      this.raffleButton.active = !this.spinning && !this.popped;
      if (this.popped) {
         String winnerText = String.format("Winner is %s!", this.winner);
         int textWidth = this.font.width(winnerText);
         this.font.drawShadow(matrixStack, winnerText, midX - textWidth / 2.0F, midY + crystalHeight / 2.0F + 33.0F, -4855553);
      }

      this.leftConfettiPopper.spawnedPosition(10, midY);
      this.rightConfettiPopper.spawnedPosition(this.width - 10, midY);
      this.leftConfettiPopper.tick();
      this.rightConfettiPopper.tick();
      this.leftConfettiPopper.render(matrixStack);
      this.rightConfettiPopper.render(matrixStack);
      if (this.spinning) {
         this.elapsedTicks += partialTicks;
      }
   }

   protected void renderWheelBackground(PoseStack matrixStack) {
      Rectangle viewportBounds = this.getViewportBounds();
      GuiComponent.fill(
         matrixStack, viewportBounds.x, viewportBounds.y, viewportBounds.x + viewportBounds.width, viewportBounds.y + viewportBounds.height, -13223617
      );
   }

   protected void renderWheel(PoseStack matrixStack) {
      Rectangle bounds = this.getViewportBounds();
      double yOffset = this.calculateYOffset(this.elapsedTicks);
      matrixStack.pushPose();
      matrixStack.translate(bounds.x, bounds.y, 0.0);
      matrixStack.translate(0.0, -yOffset, 0.0);

      for (RaffleEntry raffleWidget : this.raffleWidgets) {
         Rectangle entryBounds = raffleWidget.getBounds();
         RenderSystem.setShaderTexture(0, UI_RESOURCE);
         this.blit(matrixStack, entryBounds.x, entryBounds.y, 190, 3 + raffleWidget.getTypeIndex() * 20, 62, 20);
         boolean isTargeted = entryBounds.contains(entryBounds.x + 2, (int)(bounds.y + bounds.height / 2.0F + yOffset));
         matrixStack.pushPose();
         int stringWidth = this.font.width(raffleWidget.getOccupantName());
         float widthRatio = stringWidth / 62.0F;
         matrixStack.translate(entryBounds.x + 1, entryBounds.y + 1, 0.0);
         matrixStack.translate(entryBounds.getWidth() / 2.0, entryBounds.getHeight() / 2.0, 0.0);
         if (widthRatio > 0.9) {
            matrixStack.scale(0.9F / widthRatio, 0.9F / widthRatio, 1.0F);
         }

         matrixStack.translate(-stringWidth / 2.0F, -5.0, 0.0);
         if (isTargeted) {
            this.font.drawShadow(matrixStack, raffleWidget.getOccupantName(), 0.0F, 0.0F, -65536);
         } else {
            this.font.draw(matrixStack, raffleWidget.getOccupantName(), 0.0F, 0.0F, -13223617);
         }

         RenderSystem.enableDepthTest();
         matrixStack.popPose();
         if (entryBounds.y + entryBounds.height - yOffset < 0.0) {
            this.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.RAFFLE_SFX, 1.2F, 1.0F));
            entryBounds.y = entryBounds.y + this.raffleWidgets.size() * 21;
         }
      }

      matrixStack.popPose();
      if (this.elapsedTicks >= this.spinTicks) {
         this.spinning = false;
         if (!this.popped) {
            ModNetwork.CHANNEL.sendToServer(RaffleServerMessage.animationDone(this.winner));
            this.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.CONFETTI_SFX, 1.0F));
            this.leftConfettiPopper.pop();
            this.rightConfettiPopper.pop();
            this.popped = true;
         }
      }
   }
}
