package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.AdvancedVendingMachineScreen;
import iskallia.vault.container.AdvancedVendingContainer;
import iskallia.vault.vending.Trade;
import iskallia.vault.vending.TraderCore;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class AdvancedTradeWidget extends Widget {
   public static final int BUTTON_WIDTH = 88;
   public static final int BUTTON_HEIGHT = 27;
   protected AdvancedVendingMachineScreen parentScreen;
   protected TraderCore traderCode;

   public AdvancedTradeWidget(int x, int y, TraderCore traderCode, AdvancedVendingMachineScreen parentScreen) {
      super(x, y, 0, 0, new StringTextComponent(""));
      this.parentScreen = parentScreen;
      this.traderCode = traderCode;
   }

   public TraderCore getTraderCode() {
      return this.traderCode;
   }

   public void func_212927_b(double mouseX, double mouseY) {
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean isHovered(int mouseX, int mouseY) {
      return this.field_230690_l_ <= mouseX && mouseX <= this.field_230690_l_ + 88 && this.field_230691_m_ <= mouseY && mouseY <= this.field_230691_m_ + 27;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(AdvancedVendingMachineScreen.HUD_RESOURCE);
      Trade trade = this.traderCode.getTrade();
      ItemStack buy = trade.getBuy().toStack();
      ItemStack sell = trade.getSell().toStack();
      ItemRenderer itemRenderer = minecraft.func_175599_af();
      Rectangle tradeBoundaries = this.parentScreen.getTradeBoundaries();
      int yOFfset = this.parentScreen.tradesContainer.getyOffset();
      if (trade.getTradesLeft() == 0) {
         func_238463_a_(matrixStack, this.field_230690_l_, this.field_230691_m_, 277.0F, 96.0F, 88, 27, 512, 256);
         RenderSystem.disableDepthTest();
         itemRenderer.func_175042_a(buy, 5 + this.field_230690_l_ + tradeBoundaries.x, 6 + this.field_230691_m_ + tradeBoundaries.y - yOFfset);
         itemRenderer.func_175042_a(sell, 55 + this.field_230690_l_ + tradeBoundaries.x, 6 + this.field_230691_m_ + tradeBoundaries.y - yOFfset);
      } else {
         boolean isHovered = this.isHovered(mouseX, mouseY);
         boolean isSelected = ((AdvancedVendingContainer)this.parentScreen.func_212873_a_()).getSelectedTrade() == this.traderCode;
         func_238463_a_(matrixStack, this.field_230690_l_, this.field_230691_m_, 277.0F, !isHovered && !isSelected ? 40.0F : 68.0F, 88, 27, 512, 256);
         RenderSystem.disableDepthTest();
         itemRenderer.func_175042_a(buy, 5 + this.field_230690_l_ + tradeBoundaries.x, 6 + this.field_230691_m_ + tradeBoundaries.y - yOFfset);
         itemRenderer.func_175042_a(sell, 55 + this.field_230690_l_ + tradeBoundaries.x, 6 + this.field_230691_m_ + tradeBoundaries.y - yOFfset);
         minecraft.field_71466_p.func_238421_b_(matrixStack, buy.func_190916_E() + "", this.field_230690_l_ + 23, this.field_230691_m_ + 10, -1);
         minecraft.field_71466_p.func_238421_b_(matrixStack, sell.func_190916_E() + "", this.field_230690_l_ + 73, this.field_230691_m_ + 10, -1);
         RenderSystem.enableDepthTest();
      }
   }
}
