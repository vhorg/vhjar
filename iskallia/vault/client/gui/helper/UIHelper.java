package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;

public class UIHelper {
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability-tree.png");
   private static final int[] LINE_BREAK_VALUES = new int[]{0, 10, -10, 25, -25};

   public static void renderOverflowHidden(MatrixStack matrixStack, Consumer<MatrixStack> backgroundRenderer, Consumer<MatrixStack> innerRenderer) {
      matrixStack.func_227860_a_();
      RenderSystem.enableDepthTest();
      matrixStack.func_227861_a_(0.0, 0.0, 950.0);
      RenderSystem.colorMask(false, false, false, false);
      AbstractGui.func_238467_a_(matrixStack, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      matrixStack.func_227861_a_(0.0, 0.0, -950.0);
      RenderSystem.depthFunc(518);
      backgroundRenderer.accept(matrixStack);
      RenderSystem.depthFunc(515);
      innerRenderer.accept(matrixStack);
      RenderSystem.depthFunc(518);
      matrixStack.func_227861_a_(0.0, 0.0, -950.0);
      RenderSystem.colorMask(false, false, false, false);
      AbstractGui.func_238467_a_(matrixStack, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      matrixStack.func_227861_a_(0.0, 0.0, 950.0);
      RenderSystem.depthFunc(515);
      RenderSystem.disableDepthTest();
      matrixStack.func_227865_b_();
   }

   public static void renderContainerBorder(
      AbstractGui gui, MatrixStack matrixStack, Rectangle screenBounds, int u, int v, int lw, int rw, int th, int bh, int contentColor
   ) {
      int width = screenBounds.getWidth();
      int height = screenBounds.getHeight();
      renderContainerBorder(gui, matrixStack, screenBounds.x0, screenBounds.y0, width, height, u, v, lw, rw, th, bh, contentColor);
   }

   public static void renderContainerBorder(
      AbstractGui gui, MatrixStack matrixStack, int x, int y, int width, int height, int u, int v, int lw, int rw, int th, int bh, int contentColor
   ) {
      int horizontalGap = width - lw - rw;
      int verticalGap = height - th - bh;
      if (contentColor != 0) {
         AbstractGui.func_238467_a_(matrixStack, x + lw, y + th, x + lw + horizontalGap, y + th + verticalGap, contentColor);
      }

      gui.func_238474_b_(matrixStack, x, y, u, v, lw, th);
      gui.func_238474_b_(matrixStack, x + lw + horizontalGap, y, u + lw + 3, v, rw, th);
      gui.func_238474_b_(matrixStack, x, y + th + verticalGap, u, v + th + 3, lw, bh);
      gui.func_238474_b_(matrixStack, x + lw + horizontalGap, y + th + verticalGap, u + lw + 3, v + th + 3, rw, bh);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(x + lw, y, 0.0);
      matrixStack.func_227862_a_(horizontalGap, 1.0F, 1.0F);
      gui.func_238474_b_(matrixStack, 0, 0, u + lw + 1, v, 1, th);
      matrixStack.func_227861_a_(0.0, th + verticalGap, 0.0);
      gui.func_238474_b_(matrixStack, 0, 0, u + lw + 1, v + th + 3, 1, bh);
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(x, y + th, 0.0);
      matrixStack.func_227862_a_(1.0F, verticalGap, 1.0F);
      gui.func_238474_b_(matrixStack, 0, 0, u, v + th + 1, lw, 1);
      matrixStack.func_227861_a_(lw + horizontalGap, 0.0, 0.0);
      gui.func_238474_b_(matrixStack, 0, 0, u + lw + 3, v + th + 1, rw, 1);
      matrixStack.func_227865_b_();
   }

   public static void renderLabelAtRight(AbstractGui gui, MatrixStack matrixStack, String text, int x, int y) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(UI_RESOURCE);
      FontRenderer fontRenderer = minecraft.field_71466_p;
      int textWidth = fontRenderer.func_78256_a(text);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(x, y, 0.0);
      float scale = 0.75F;
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227861_a_(-9.0, 0.0, 0.0);
      gui.func_238474_b_(matrixStack, 0, 0, 143, 36, 9, 24);
      int gap = 5;
      int remainingWidth = textWidth + 2 * gap;
      matrixStack.func_227861_a_(-remainingWidth, 0.0, 0.0);

      while (remainingWidth > 0) {
         gui.func_238474_b_(matrixStack, 0, 0, 136, 36, 6, 24);
         remainingWidth -= 6;
         matrixStack.func_227861_a_(Math.min(6, remainingWidth), 0.0, 0.0);
      }

      matrixStack.func_227861_a_(-textWidth - 2 * gap - 6, 0.0, 0.0);
      gui.func_238474_b_(matrixStack, 0, 0, 121, 36, 14, 24);
      fontRenderer.func_238421_b_(matrixStack, text, 14 + gap, 9.0F, -12305893);
      matrixStack.func_227865_b_();
   }

   public static int renderWrappedText(MatrixStack matrixStack, IFormattableTextComponent text, int maxWidth, int padding) {
      Minecraft minecraft = Minecraft.func_71410_x();
      FontRenderer fontRenderer = minecraft.field_71466_p;
      List<ITextProperties> lines = getLines(TextComponentUtils.func_240648_a_(text.func_230532_e_(), text.func_150256_b()), maxWidth - 3 * padding);
      List<IReorderingProcessor> processors = LanguageMap.func_74808_a().func_244260_a(lines);

      for (int i = 0; i < processors.size(); i++) {
         fontRenderer.func_238422_b_(matrixStack, processors.get(i), padding, 10 * i + padding, -15130590);
      }

      return processors.size();
   }

   private static List<ITextProperties> getLines(ITextComponent component, int maxWidth) {
      Minecraft minecraft = Minecraft.func_71410_x();
      CharacterManager charactermanager = minecraft.field_71466_p.func_238420_b_();
      List<ITextProperties> list = null;
      float f = Float.MAX_VALUE;

      for (int i : LINE_BREAK_VALUES) {
         List<ITextProperties> list1 = charactermanager.func_238362_b_(component, maxWidth - i, Style.field_240709_b_);
         float f1 = Math.abs(getTextWidth(charactermanager, list1) - maxWidth);
         if (f1 <= 10.0F) {
            return list1;
         }

         if (f1 < f) {
            f = f1;
            list = list1;
         }
      }

      return list;
   }

   private static float getTextWidth(CharacterManager manager, List<ITextProperties> text) {
      return (float)text.stream().mapToDouble(manager::func_238356_a_).max().orElse(0.0);
   }
}
