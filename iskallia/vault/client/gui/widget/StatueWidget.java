package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.StatueCauldronScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class StatueWidget extends Widget {
   public static final int BUTTON_WIDTH = 88;
   public static final int BUTTON_HEIGHT = 27;
   protected StatueCauldronScreen parentScreen;
   protected String name;
   protected int count;

   public StatueWidget(int x, int y, String name, int count, StatueCauldronScreen parentScreen) {
      super(x, y, 0, 0, new StringTextComponent(""));
      this.parentScreen = parentScreen;
      this.name = name;
      this.count = count;
   }

   public boolean isHovered(int mouseX, int mouseY) {
      return this.field_230690_l_ <= mouseX && mouseX <= this.field_230690_l_ + 88 && this.field_230691_m_ <= mouseY && mouseY <= this.field_230691_m_ + 27;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(StatueCauldronScreen.HUD_RESOURCE);
      boolean isSelected = this.parentScreen.getSelected().getLatestNickname().equalsIgnoreCase(this.name);
      boolean isHovered = this.isHovered(mouseX, mouseY);
      func_238463_a_(matrixStack, this.field_230690_l_, this.field_230691_m_, 225.0F, !isHovered && !isSelected ? 40.0F : 68.0F, 88, 27, 512, 256);
      RenderSystem.disableDepthTest();
      StringTextComponent nameText = new StringTextComponent(this.name);
      float startXname = 44.0F - minecraft.field_71466_p.func_78256_a(nameText.getString()) / 2.0F;
      minecraft.field_71466_p.func_243248_b(matrixStack, nameText, startXname, this.field_230691_m_ + 4, -1);
      StringTextComponent countText = new StringTextComponent("(" + this.count + ")");
      float startXcount = 44.0F - minecraft.field_71466_p.func_78256_a(countText.getString()) / 2.0F;
      minecraft.field_71466_p.func_243248_b(matrixStack, countText, startXcount, this.field_230691_m_ + 14, -1);
      RenderSystem.enableDepthTest();
   }

   public String getName() {
      return this.name;
   }
}
