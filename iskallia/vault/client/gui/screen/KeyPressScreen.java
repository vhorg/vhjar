package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.container.KeyPressContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeyPressScreen extends AbstractContainerScreen<KeyPressContainer> {
   private static final ResourceLocation GUI_RESOURCE = VaultMod.id("textures/gui/key_press.png");

   public KeyPressScreen(KeyPressContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
   }

   protected void renderLabels(PoseStack matrixStack, int x, int y) {
      this.font.draw(matrixStack, new TextComponent(""), this.titleLabelX, this.titleLabelY, 4210752);
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      float midX = this.width / 2.0F;
      float midY = this.height / 2.0F;
      Minecraft minecraft = this.getMinecraft();
      int containerWidth = 176;
      int containerHeight = 166;
      RenderSystem.setShaderTexture(0, GUI_RESOURCE);
      this.blit(matrixStack, (int)(midX - containerWidth / 2), (int)(midY - containerHeight / 2), 0, 0, containerWidth, containerHeight);
      Font fontRenderer = minecraft.font;
      String title = "Mold Vault Keys";
      fontRenderer.draw(matrixStack, title, midX - 35.0F, midY - 63.0F, 4144959);
      String inventoryTitle = "Inventory";
      fontRenderer.draw(matrixStack, inventoryTitle, midX - 80.0F, midY - 11.0F, 4144959);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderTooltip(matrixStack, mouseX, mouseY);
   }
}
