package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.container.VaultCharmControllerContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultCharmControllerScrollMessage;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class VaultCharmControllerScreen extends ContainerScreen<VaultCharmControllerContainer> {
   private static final ResourceLocation TEXTURE = Vault.id("textures/gui/vault_charm_controller.png");
   private float scrollDelta;
   private float currentScroll;
   private boolean isScrolling;

   public VaultCharmControllerScreen(VaultCharmControllerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
      this.field_146999_f = 195;
      this.field_147000_g = 222;
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int offsetX = (this.field_230708_k_ - this.field_146999_f) / 2;
      int offsetY = (this.field_230709_l_ - this.field_147000_g) / 2;
      this.func_238474_b_(matrixStack, offsetX, offsetY, 0, 0, this.field_146999_f, this.field_147000_g);
      int drawnSlots = 0;

      for (Slot slot : ((VaultCharmControllerContainer)this.field_147002_h).field_75151_b) {
         if (slot.field_75222_d > 35) {
            this.func_238474_b_(matrixStack, offsetX + slot.field_75223_e - 1, offsetY + slot.field_75221_f - 1, 195, 0, 18, 18);
            if (drawnSlots++ == 54) {
               return;
            }
         }
      }
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      String title = "Charm Inscription - "
         + ((VaultCharmControllerContainer)this.field_147002_h).getCurrentAmountWhitelisted()
         + "/"
         + ((VaultCharmControllerContainer)this.field_147002_h).getInventorySize()
         + " slots";
      this.field_230712_o_.func_243248_b(matrixStack, new StringTextComponent(title), 5.0F, 5.0F, 4210752);
      if (this.needsScrollBars()) {
         this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
         this.func_238474_b_(matrixStack, 175, 18 + (int)(95.0F * this.currentScroll), 195 + (this.needsScrollBars() ? 0 : 12), 19, 12, 15);
      }
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   public boolean func_231177_au__() {
      return false;
   }

   private boolean needsScrollBars() {
      return ((VaultCharmControllerContainer)this.field_147002_h).canScroll();
   }

   private boolean scrollBarClicked(double mouseX, double mouseY) {
      int scrollLeft = this.field_147003_i + 175;
      int scrollTop = this.field_147009_r + 18;
      int scrollRight = scrollLeft + 12;
      int scrollBottom = scrollTop + 110;
      return mouseX >= scrollLeft && mouseY >= scrollTop && mouseX < scrollRight && mouseY < scrollBottom;
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      if (this.scrollBarClicked(mouseX, mouseY)) {
         this.isScrolling = true;
         return true;
      } else {
         return super.func_231044_a_(mouseX, mouseY, button);
      }
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      this.isScrolling = false;
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      if (!this.needsScrollBars()) {
         return false;
      } else {
         int i = ((VaultCharmControllerContainer)this.field_147002_h).getInventorySize() / 9 - 6;
         this.currentScroll = (float)(this.currentScroll - delta / i);
         this.currentScroll = MathHelper.func_76131_a(this.currentScroll, 0.0F, 1.0F);
         ModNetwork.CHANNEL.sendToServer(new VaultCharmControllerScrollMessage(this.currentScroll));
         ((VaultCharmControllerContainer)this.field_147002_h).scrollTo(this.currentScroll);
         return true;
      }
   }

   public boolean func_231045_a_(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.isScrolling) {
         int top = this.field_147009_r + 18;
         int bottom = top + 110;
         this.currentScroll = ((float)mouseY - top - 7.5F) / (bottom - top - 15.0F);
         this.currentScroll = MathHelper.func_76131_a(this.currentScroll, 0.0F, 1.0F);
         int intervals = ((VaultCharmControllerContainer)this.field_147002_h).getInventorySize() / 9 - 6;
         float scroll = (float)Math.round(this.currentScroll * intervals) / intervals;
         if (scroll != this.scrollDelta) {
            ModNetwork.CHANNEL.sendToServer(new VaultCharmControllerScrollMessage(scroll));
            ((VaultCharmControllerContainer)this.field_147002_h).scrollTo(scroll);
            this.scrollDelta = scroll;
         }

         return true;
      } else {
         return super.func_231045_a_(mouseX, mouseY, button, dragX, dragY);
      }
   }
}
