package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.container.VaultCrateContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class VaultCrateScreen extends ContainerScreen<VaultCrateContainer> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

   public VaultCrateScreen(VaultCrateContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
      this.field_147000_g = 222;
      this.field_238745_s_ = this.field_147000_g - 94;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int i = (this.field_230708_k_ - this.field_146999_f) / 2;
      int j = (this.field_230709_l_ - this.field_147000_g) / 2;
      this.func_238474_b_(matrixStack, i, j, 0, 0, this.field_146999_f, 125);
      this.func_238474_b_(matrixStack, i, j + 108 + 17, 0, 126, this.field_146999_f, 96);
   }
}
