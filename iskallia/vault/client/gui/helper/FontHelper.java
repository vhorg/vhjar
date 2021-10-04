package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class FontHelper {
   public static float drawStringWithBorder(MatrixStack matrixStack, String text, float x, float y, int color, int borderColor) {
      return drawStringWithBorder(matrixStack, new StringTextComponent(text), x, y, color, borderColor);
   }

   public static float drawStringWithBorder(MatrixStack matrixStack, ITextComponent text, float x, float y, int color, int borderColor) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.field_71466_p.func_243248_b(matrixStack, text, x - 1.0F, y, borderColor);
      minecraft.field_71466_p.func_243248_b(matrixStack, text, x + 1.0F, y, borderColor);
      minecraft.field_71466_p.func_243248_b(matrixStack, text, x, y - 1.0F, borderColor);
      minecraft.field_71466_p.func_243248_b(matrixStack, text, x, y + 1.0F, borderColor);
      return minecraft.field_71466_p.func_243248_b(matrixStack, text, x, y, color) + 1;
   }

   public static int drawTextComponent(MatrixStack matrixStack, ITextComponent component, boolean rightAligned) {
      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      int width = fontRenderer.func_238414_a_(component);
      fontRenderer.func_243246_a(matrixStack, component, rightAligned ? -width : 0.0F, 0.0F, -1052689);
      return width;
   }
}
