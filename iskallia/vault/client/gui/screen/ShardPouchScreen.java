package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.container.inventory.ShardPouchContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ShardPouchScreen extends AbstractContainerScreen<ShardPouchContainer> {
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/shard_pouch.png");

   public ShardPouchScreen(ShardPouchContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.imageWidth = 176;
      this.imageHeight = 137;
      this.titleLabelX = 33;
      this.inventoryLabelY = 45;
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int offsetX = (this.width - this.imageWidth) / 2;
      int offsetY = (this.height - this.imageHeight) / 2;
      this.blit(matrixStack, offsetX, offsetY, 0, 0, this.imageWidth, this.imageHeight);
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderTooltip(matrixStack, mouseX, mouseY);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
