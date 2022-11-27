package iskallia.vault.client.gui.framework.text;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public final class TextBorder {
   public static final TextColor DEFAULT_COLOR = TextColor.parseColor("#FFFFFF");
   public static final TextColor DEFAULT_SHADOW_COLOR = TextColor.parseColor("#000000");
   public static final TextColor DEFAULT_BORDER_COLOR = TextColor.parseColor("#000000");
   public static final Supplier<Font> DEFAULT_FONT = () -> Minecraft.getInstance().font;
   private static final TextBorder.None NONE = new TextBorder.None();

   public static TextBorder.None none() {
      return NONE;
   }

   public static TextBorder.Shadow shadow() {
      return new TextBorder.Shadow(DEFAULT_SHADOW_COLOR);
   }

   public static TextBorder.Border4 border4() {
      return new TextBorder.Border4(DEFAULT_BORDER_COLOR);
   }

   public static TextBorder.Border8 border8() {
      return new TextBorder.Border8(DEFAULT_BORDER_COLOR);
   }

   private TextBorder() {
   }

   public abstract static class AbstractBorder<T extends TextBorder.AbstractBorder<?>> implements TextBorder.ITextBorder {
      protected TextColor borderColor;
      protected Component component;
      protected Component borderComponent;

      public AbstractBorder(TextColor borderColor) {
         this.borderColor = borderColor;
      }

      public TextColor color() {
         return this.borderColor;
      }

      public T color(TextColor color) {
         this.borderColor = color;
         return (T)this;
      }

      protected Component deepCopy(Component component) {
         MutableComponent copy = component.plainCopy();
         copy.withStyle(component.getStyle());

         for (Component sibling : component.getSiblings()) {
            copy.append(this.deepCopy(sibling));
         }

         return copy;
      }

      protected Component setComponentColor(Component component, TextColor color) {
         if (component instanceof MutableComponent mutableComponent) {
            mutableComponent.withStyle(mutableComponent.getStyle().withColor(color));
         }

         for (Component sibling : component.getSiblings()) {
            if (sibling instanceof MutableComponent mutableComponent) {
               this.setComponentColor(mutableComponent, color);
            }
         }

         return component;
      }

      @Override
      public void render(
         IElementRenderer renderer,
         @NotNull PoseStack poseStack,
         Component component,
         TextWrap.ITextWrap textWrap,
         TextAlign.ITextAlign textAlign,
         int x,
         int y,
         int z,
         int width
      ) {
         if (!component.equals(this.component)) {
            this.component = component;
            this.borderComponent = this.setComponentColor(this.deepCopy(component), this.borderColor);
         }

         List<FormattedCharSequence> formattedCharSequenceList = textWrap.process(this.component, width);
         List<FormattedCharSequence> formattedCharSequenceBorderList = textWrap.process(this.borderComponent, width);
         if (formattedCharSequenceList.size() == formattedCharSequenceBorderList.size()) {
            for (int i = 0; i < formattedCharSequenceList.size(); i++) {
               FormattedCharSequence formattedCharSequence = formattedCharSequenceList.get(i);
               FormattedCharSequence formattedCharSequenceBorder = formattedCharSequenceBorderList.get(i);
               Font font = TextBorder.DEFAULT_FONT.get();
               this.render(
                  renderer,
                  formattedCharSequence,
                  formattedCharSequenceBorder,
                  font,
                  poseStack,
                  textAlign.calculateX(x, font.width(formattedCharSequence), width) + 1,
                  y + 9 * i,
                  z,
                  TextBorder.DEFAULT_COLOR.getValue(),
                  this.borderColor.getValue()
               );
            }
         }
      }

      protected abstract void render(
         IElementRenderer var1,
         FormattedCharSequence var2,
         FormattedCharSequence var3,
         Font var4,
         PoseStack var5,
         int var6,
         int var7,
         int var8,
         int var9,
         int var10
      );
   }

   public static class Border4 extends TextBorder.AbstractBorder<TextBorder.Border4> {
      protected Border4(TextColor borderColor) {
         super(borderColor);
      }

      @Override
      protected void render(
         IElementRenderer renderer,
         FormattedCharSequence text,
         FormattedCharSequence border,
         Font font,
         PoseStack poseStack,
         int x,
         int y,
         int z,
         int color,
         int borderColor
      ) {
         renderer.renderBorder4(text, border, font, poseStack, x, y, z, color, borderColor);
      }
   }

   public static class Border8 extends TextBorder.AbstractBorder<TextBorder.Border8> {
      protected Border8(TextColor borderColor) {
         super(borderColor);
      }

      @Override
      protected void render(
         IElementRenderer renderer,
         FormattedCharSequence text,
         FormattedCharSequence border,
         Font font,
         PoseStack poseStack,
         int x,
         int y,
         int z,
         int color,
         int borderColor
      ) {
         renderer.renderBorder8(text, border, font, poseStack, x, y, z, color, borderColor);
      }
   }

   @FunctionalInterface
   public interface ITextBorder {
      void render(
         IElementRenderer var1,
         @NotNull PoseStack var2,
         Component var3,
         TextWrap.ITextWrap var4,
         TextAlign.ITextAlign var5,
         int var6,
         int var7,
         int var8,
         int var9
      );
   }

   public static class None implements TextBorder.ITextBorder {
      protected None() {
      }

      @Override
      public void render(
         IElementRenderer renderer,
         @NotNull PoseStack poseStack,
         Component component,
         TextWrap.ITextWrap textWrap,
         TextAlign.ITextAlign textAlign,
         int x,
         int y,
         int z,
         int width
      ) {
         Font font = TextBorder.DEFAULT_FONT.get();
         List<FormattedCharSequence> formattedCharSequenceList = textWrap.process(component, width);

         for (int i = 0; i < formattedCharSequenceList.size(); i++) {
            FormattedCharSequence formattedCharSequence = formattedCharSequenceList.get(i);
            renderer.renderPlain(
               formattedCharSequence,
               font,
               poseStack,
               textAlign.calculateX(x, font.width(formattedCharSequence), width),
               y + 9 * i,
               z,
               TextBorder.DEFAULT_COLOR.getValue()
            );
         }
      }
   }

   public static class Shadow extends TextBorder.AbstractBorder<TextBorder.Shadow> {
      protected Shadow(TextColor shadowColor) {
         super(shadowColor);
      }

      @Override
      protected void render(
         IElementRenderer renderer,
         FormattedCharSequence text,
         FormattedCharSequence border,
         Font font,
         PoseStack poseStack,
         int x,
         int y,
         int z,
         int color,
         int borderColor
      ) {
         renderer.renderShadow(text, border, font, poseStack, x, y, z, color, borderColor);
      }
   }
}
