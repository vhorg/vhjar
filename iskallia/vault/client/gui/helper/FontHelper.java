package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;

public class FontHelper {
   public static void drawStringWithBorder(MatrixStack matrixStack, String text, float x, float y, int color, int borderColor) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.field_71466_p.func_238421_b_(matrixStack, text, x - 1.0F, y, borderColor);
      minecraft.field_71466_p.func_238421_b_(matrixStack, text, x + 1.0F, y, borderColor);
      minecraft.field_71466_p.func_238421_b_(matrixStack, text, x, y - 1.0F, borderColor);
      minecraft.field_71466_p.func_238421_b_(matrixStack, text, x, y + 1.0F, borderColor);
      minecraft.field_71466_p.func_238421_b_(matrixStack, text, x, y, color);
   }
}
