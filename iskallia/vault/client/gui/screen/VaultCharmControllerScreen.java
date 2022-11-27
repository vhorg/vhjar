package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.container.VaultCharmControllerContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultCharmControllerScrollMessage;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class VaultCharmControllerScreen extends AbstractContainerScreen<VaultCharmControllerContainer> {
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/vault_charm_controller.png");
   private float scrollDelta;
   private float currentScroll;
   private boolean isScrolling;

   public VaultCharmControllerScreen(VaultCharmControllerContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.imageWidth = 195;
      this.imageHeight = 222;
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int offsetX = (this.width - this.imageWidth) / 2;
      int offsetY = (this.height - this.imageHeight) / 2;
      this.blit(matrixStack, offsetX, offsetY, 0, 0, this.imageWidth, this.imageHeight);
      int drawnSlots = 0;

      for (Slot slot : ((VaultCharmControllerContainer)this.menu).slots) {
         if (slot.index > 35) {
            this.blit(matrixStack, offsetX + slot.x - 1, offsetY + slot.y - 1, 195, 0, 18, 18);
            if (drawnSlots++ == 54) {
               return;
            }
         }
      }
   }

   protected void renderLabels(PoseStack matrixStack, int x, int y) {
      String title = "Charm Inscription - "
         + ((VaultCharmControllerContainer)this.menu).getCurrentAmountWhitelisted()
         + "/"
         + ((VaultCharmControllerContainer)this.menu).getInventorySize()
         + " slots";
      this.font.draw(matrixStack, new TextComponent(title), 5.0F, 5.0F, 4210752);
      if (this.needsScrollBars()) {
         RenderSystem.setShaderTexture(0, TEXTURE);
         this.blit(matrixStack, 175, 18 + (int)(95.0F * this.currentScroll), 195 + (this.needsScrollBars() ? 0 : 12), 19, 12, 15);
      }
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderTooltip(matrixStack, mouseX, mouseY);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private boolean needsScrollBars() {
      return ((VaultCharmControllerContainer)this.menu).canScroll();
   }

   private boolean scrollBarClicked(double mouseX, double mouseY) {
      int scrollLeft = this.leftPos + 175;
      int scrollTop = this.topPos + 18;
      int scrollRight = scrollLeft + 12;
      int scrollBottom = scrollTop + 110;
      return mouseX >= scrollLeft && mouseY >= scrollTop && mouseX < scrollRight && mouseY < scrollBottom;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.scrollBarClicked(mouseX, mouseY)) {
         this.isScrolling = true;
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.isScrolling = false;
      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      if (!this.needsScrollBars()) {
         return false;
      } else {
         int i = ((VaultCharmControllerContainer)this.menu).getInventorySize() / 9 - 6;
         this.currentScroll = (float)(this.currentScroll - delta / i);
         this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
         ModNetwork.CHANNEL.sendToServer(new VaultCharmControllerScrollMessage(this.currentScroll));
         ((VaultCharmControllerContainer)this.menu).scrollTo(this.currentScroll);
         return true;
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.isScrolling) {
         int top = this.topPos + 18;
         int bottom = top + 110;
         this.currentScroll = ((float)mouseY - top - 7.5F) / (bottom - top - 15.0F);
         this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
         int intervals = ((VaultCharmControllerContainer)this.menu).getInventorySize() / 9 - 6;
         float scroll = (float)Math.round(this.currentScroll * intervals) / intervals;
         if (scroll != this.scrollDelta) {
            ModNetwork.CHANNEL.sendToServer(new VaultCharmControllerScrollMessage(scroll));
            ((VaultCharmControllerContainer)this.menu).scrollTo(scroll);
            this.scrollDelta = scroll;
         }

         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }
}
