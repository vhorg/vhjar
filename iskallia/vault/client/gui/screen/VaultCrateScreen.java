package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.container.VaultCrateContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VaultCrateScreen extends AbstractContainerScreen<VaultCrateContainer> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

   public VaultCrateScreen(VaultCrateContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.imageHeight = 222;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderTooltip(matrixStack, mouseX, mouseY);
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(matrixStack, i, j, 0, 0, this.imageWidth, 125);
      this.blit(matrixStack, i, j + 108 + 17, 0, 126, this.imageWidth, 96);
   }
}
