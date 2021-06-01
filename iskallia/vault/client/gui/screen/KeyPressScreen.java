package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.container.KeyPressContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class KeyPressScreen extends ContainerScreen<KeyPressContainer> {
   private static final ResourceLocation GUI_RESOURCE = Vault.id("textures/gui/key-press.png");

   public KeyPressScreen(KeyPressContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      this.field_230712_o_.func_243248_b(matrixStack, new StringTextComponent(""), this.field_238742_p_, this.field_238743_q_, 4210752);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      float midX = this.field_230708_k_ / 2.0F;
      float midY = this.field_230709_l_ / 2.0F;
      Minecraft minecraft = this.getMinecraft();
      int containerWidth = 176;
      int containerHeight = 166;
      minecraft.func_110434_K().func_110577_a(GUI_RESOURCE);
      this.func_238474_b_(matrixStack, (int)(midX - containerWidth / 2), (int)(midY - containerHeight / 2), 0, 0, containerWidth, containerHeight);
      FontRenderer fontRenderer = minecraft.field_71466_p;
      String title = "Mold Vault Keys";
      fontRenderer.func_238421_b_(matrixStack, title, midX - 35.0F, midY - 63.0F, 4144959);
      String inventoryTitle = "Inventory";
      fontRenderer.func_238421_b_(matrixStack, inventoryTitle, midX - 80.0F, midY - 11.0F, 4144959);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }
}
