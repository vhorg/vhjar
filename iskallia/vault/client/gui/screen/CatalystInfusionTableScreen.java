package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.container.inventory.CatalystInfusionTableContainer;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CatalystInfusionTableScreen extends AbstractContainerScreen<CatalystInfusionTableContainer> {
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/catalyst_infusion_table.png");

   public CatalystInfusionTableScreen(CatalystInfusionTableContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.imageWidth = 176;
      this.imageHeight = 166;
   }

   protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int offsetX = (this.width - this.imageWidth) / 2;
      int offsetY = (this.height - this.imageHeight) / 2;
      this.blit(poseStack, offsetX, offsetY, 0, 0, this.imageWidth, this.imageHeight);
      if (((CatalystInfusionTableContainer)this.menu).isActive()) {
         float progress = ((CatalystInfusionTableContainer)this.menu).getProgress();
         this.blit(poseStack, offsetX + 79, offsetY + 34, 176, 0, (int)(24.0F * progress), 16);
      }
   }

   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(poseStack);
      super.render(poseStack, mouseX, mouseY, partialTicks);
      this.renderTooltip(poseStack, mouseX, mouseY);
   }
}
