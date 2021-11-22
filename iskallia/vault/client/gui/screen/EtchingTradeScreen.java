package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import iskallia.vault.entity.EtchingVendorEntity;
import iskallia.vault.init.ModItems;
import java.awt.Rectangle;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EtchingTradeScreen extends ContainerScreen<EtchingTradeContainer> {
   private static final ResourceLocation TEXTURE = Vault.id("textures/gui/etching_trade.png");

   public EtchingTradeScreen(EtchingTradeContainer screenContainer, PlayerInventory inv, ITextComponent title) {
      super(screenContainer, inv, StringTextComponent.field_240750_d_);
      this.field_146999_f = 176;
      this.field_147000_g = 184;
      this.field_238745_s_ = 90;
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int offsetX = (this.field_230708_k_ - this.field_146999_f) / 2;
      int offsetY = (this.field_230709_l_ - this.field_147000_g) / 2;
      func_238464_a_(matrixStack, offsetX, offsetY, this.func_230927_p_(), 0.0F, 0.0F, this.field_146999_f, this.field_147000_g, 256, 512);
      EtchingTradeContainer container = (EtchingTradeContainer)this.func_212873_a_();
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

               func_238464_a_(matrixStack, xx, yy, this.func_230927_p_(), 177.0F, vOffset, 88, 27, 256, 512);
               func_238464_a_(matrixStack, slotInXX, slotInYY, this.func_230927_p_(), 177.0F, 85.0F, 18, 18, 256, 512);
               func_238464_a_(matrixStack, slotOutXX, slotOutYY, this.func_230927_p_(), 177.0F, 85.0F, 18, 18, 256, 512);
            }
         }
      }
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      super.func_230451_b_(matrixStack, x, y);
      EtchingTradeContainer container = (EtchingTradeContainer)this.func_212873_a_();
      EtchingVendorEntity vendor = container.getVendor();
      if (vendor != null) {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile != null) {
            for (int i = 0; i < 3; i++) {
               EtchingVendorControllerTileEntity.EtchingTrade trade = controllerTile.getTrade(i);
               if (trade != null && !trade.isSold()) {
                  int xx = 71;
                  int yy = 10 + i * 28;
                  ItemStack stack = new ItemStack(ModItems.VAULT_PLATINUM, trade.getRequiredPlatinum());
                  this.field_230707_j_.func_175042_a(stack, xx, yy);
                  this.field_230707_j_.func_180453_a(this.field_230712_o_, stack, xx, yy, null);
               }
            }
         }
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
}
