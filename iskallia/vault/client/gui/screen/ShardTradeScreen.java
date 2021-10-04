package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;

public class ShardTradeScreen extends ContainerScreen<ShardTradeContainer> {
   private static final ResourceLocation TEXTURE = Vault.id("textures/gui/shard_trade.png");

   public ShardTradeScreen(ShardTradeContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
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

      for (int tradeIndex = 0; tradeIndex < 3; tradeIndex++) {
         int xx = offsetX + 83;
         int yy = offsetY + 5 + tradeIndex * 28;
         int slotXX = offsetX + 145;
         int slotYY = offsetY + 9 + tradeIndex * 28;
         int vOffset = 1;
         if (ClientShardTradeData.getTradeInfo(tradeIndex) == null) {
            vOffset = 57;
         } else {
            Rectangle tradeBox = new Rectangle(xx, yy, 88, 27);
            if (tradeBox.contains(x, y)) {
               vOffset = 29;
            }
         }

         func_238464_a_(matrixStack, xx, yy, this.func_230927_p_(), 177.0F, vOffset, 88, 27, 256, 512);
         func_238464_a_(matrixStack, slotXX, slotYY, this.func_230927_p_(), 177.0F, 85.0F, 18, 18, 256, 512);
      }
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.field_238742_p_, this.field_238743_q_, 0.0);
      matrixStack.func_227862_a_(0.75F, 0.75F, 1.0F);
      this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, 0.0F, 0.0F, 4210752);
      matrixStack.func_227865_b_();
      this.field_230712_o_.func_243248_b(matrixStack, this.field_213127_e.func_145748_c_(), this.field_238744_r_, this.field_238745_s_, 4210752);
      int shardCount = ItemShardPouch.getShardCount(Minecraft.func_71410_x().field_71439_g.field_71071_by);
      ItemStack stack = new ItemStack(ModItems.SOUL_SHARD);

      for (int tradeIndex = 0; tradeIndex < 3; tradeIndex++) {
         Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(tradeIndex);
         if (trade != null) {
            int xx = 94;
            int yy = 10 + tradeIndex * 28;
            this.field_230707_j_.func_175042_a(stack, xx, yy);
            String text = String.valueOf(trade.func_76340_b());
            int width = this.field_230712_o_.func_78256_a(text);
            int color = 16777215;
            if (shardCount < (Integer)trade.func_76340_b()) {
               color = 8257536;
            }

            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0.0, 0.0, 400.0);
            FontHelper.drawStringWithBorder(matrixStack, text, xx + 8 - width / 2.0F, (float)(yy + 8), color, 0);
            matrixStack.func_227865_b_();
         }
      }

      int xx = 34;
      int yy = 56;
      this.field_230707_j_.func_175042_a(stack, xx, yy);
      String text = String.valueOf(ClientShardTradeData.getRandomTradeCost());
      int width = this.field_230712_o_.func_78256_a(text);
      int color = 16777215;
      if (shardCount < ClientShardTradeData.getRandomTradeCost()) {
         color = 8257536;
      }

      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 0.0, 400.0);
      FontHelper.drawStringWithBorder(matrixStack, text, xx + 9 - width / 2.0F, (float)(yy + 8), color, 0);
      matrixStack.func_227865_b_();
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      this.field_146993_M = false;
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public boolean func_231177_au__() {
      return false;
   }
}
