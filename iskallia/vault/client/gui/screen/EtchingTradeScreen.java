package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import iskallia.vault.entity.entity.EtchingVendorEntity;
import iskallia.vault.init.ModBlocks;
import java.awt.Rectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class EtchingTradeScreen extends AbstractContainerScreen<EtchingTradeContainer> {
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/etching_trade.png");

   public EtchingTradeScreen(EtchingTradeContainer screenContainer, Inventory inv, Component title) {
      super(screenContainer, inv, TextComponent.EMPTY);
      this.imageWidth = 176;
      this.imageHeight = 184;
      this.inventoryLabelY = 90;
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int offsetX = (this.width - this.imageWidth) / 2;
      int offsetY = (this.height - this.imageHeight) / 2;
      blit(matrixStack, offsetX, offsetY, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 512);
      EtchingTradeContainer container = (EtchingTradeContainer)this.getMenu();
      EtchingVendorEntity vendor = container.getVendor();
      if (vendor != null) {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile != null) {
            for (int i = 0; i < 3; i++) {
               int xx = offsetX + 44;
               int yy = offsetY + 5 + i * 28;
               int slotInXX = offsetX + 52;
               int slotInYY = offsetY + 9 + i * 28;
               int slotOutXX = offsetX + 106;
               int slotOutYY = offsetY + 9 + i * 28;
               int vOffset = 1;
               EtchingVendorControllerTileEntity.EtchingTrade trade = controllerTile.getTrade(i);
               if (trade != null && !trade.isSold()) {
                  Rectangle tradeBox = new Rectangle(xx, yy, 88, 27);
                  if (tradeBox.contains(x, y)) {
                     vOffset = 29;
                  }
               } else {
                  vOffset = 57;
               }

               blit(matrixStack, xx, yy, this.getBlitOffset(), 177.0F, vOffset, 88, 27, 256, 512);
               blit(matrixStack, slotInXX, slotInYY, this.getBlitOffset(), 177.0F, 85.0F, 18, 18, 256, 512);
               blit(matrixStack, slotOutXX, slotOutYY, this.getBlitOffset(), 177.0F, 85.0F, 18, 18, 256, 512);
            }
         }
      }
   }

   protected void renderLabels(PoseStack matrixStack, int x, int y) {
      super.renderLabels(matrixStack, x, y);
      EtchingTradeContainer container = (EtchingTradeContainer)this.getMenu();
      EtchingVendorEntity vendor = container.getVendor();
      if (vendor != null) {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile != null) {
            for (int i = 0; i < 3; i++) {
               EtchingVendorControllerTileEntity.EtchingTrade trade = controllerTile.getTrade(i);
               if (trade != null && !trade.isSold()) {
                  int xx = 71;
                  int yy = 10 + i * 28;
                  ItemStack stack = new ItemStack(ModBlocks.VAULT_PLATINUM, trade.getRequiredPlatinum());
                  this.itemRenderer.renderGuiItem(stack, xx, yy);
                  this.itemRenderer.renderGuiItemDecorations(this.font, stack, xx, yy, null);
               }
            }
         }
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
}
