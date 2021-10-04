package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.container.inventory.ShardPouchContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ShardPouchScreen extends ContainerScreen<ShardPouchContainer> {
   private static final ResourceLocation TEXTURE = Vault.id("textures/gui/shard_pouch.png");

   public ShardPouchScreen(ShardPouchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
      this.field_146999_f = 176;
      this.field_147000_g = 137;
      this.field_238742_p_ = 33;
      this.field_238745_s_ = 45;
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int offsetX = (this.field_230708_k_ - this.field_146999_f) / 2;
      int offsetY = (this.field_230709_l_ - this.field_147000_g) / 2;
      this.func_238474_b_(matrixStack, offsetX, offsetY, 0, 0, this.field_146999_f, this.field_147000_g);
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
