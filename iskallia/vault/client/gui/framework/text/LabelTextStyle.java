package iskallia.vault.client.gui.framework.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public record LabelTextStyle(TextBorder.ITextBorder textBorder, TextWrap.ITextWrap textWrap, TextAlign.ITextAlign textAlign) {
   public static LabelTextStyle.Builder defaultStyle() {
      return new LabelTextStyle.Builder();
   }

   public static LabelTextStyle.Builder shadow() {
      return new LabelTextStyle.Builder().border(TextBorder.shadow());
   }

   public static LabelTextStyle.Builder shadow(ChatFormatting color) {
      return new LabelTextStyle.Builder().border(TextBorder.shadow().color(TextColor.fromLegacyFormat(color)));
   }

   public static LabelTextStyle.Builder shadow(TextColor color) {
      return new LabelTextStyle.Builder().border(TextBorder.shadow().color(color));
   }

   public static LabelTextStyle.Builder border4() {
      return new LabelTextStyle.Builder().border(TextBorder.border4());
   }

   public static LabelTextStyle.Builder border4(ChatFormatting color) {
      return new LabelTextStyle.Builder().border(TextBorder.border4().color(TextColor.fromLegacyFormat(color)));
   }

   public static LabelTextStyle.Builder border4(TextColor color) {
      return new LabelTextStyle.Builder().border(TextBorder.border4().color(color));
   }

   public static LabelTextStyle.Builder border8() {
      return new LabelTextStyle.Builder().border(TextBorder.border8());
   }

   public static LabelTextStyle.Builder border8(ChatFormatting color) {
      return new LabelTextStyle.Builder().border(TextBorder.border8().color(TextColor.fromLegacyFormat(color)));
   }

   public static LabelTextStyle.Builder border8(TextColor color) {
      return new LabelTextStyle.Builder().border(TextBorder.border8().color(color));
   }

   public static LabelTextStyle.Builder border(TextBorder.ITextBorder textBorder) {
      return new LabelTextStyle.Builder().border(textBorder);
   }

   public static LabelTextStyle.Builder wrap() {
      return new LabelTextStyle.Builder().wrap(TextWrap.wrap());
   }

   public static LabelTextStyle.Builder wrap(TextWrap.ITextWrap textWrap) {
      return new LabelTextStyle.Builder().wrap(textWrap);
   }

   public static LabelTextStyle.Builder left() {
      return new LabelTextStyle.Builder().left();
   }

   public static LabelTextStyle.Builder center() {
      return new LabelTextStyle.Builder().center();
   }

   public static LabelTextStyle.Builder right() {
      return new LabelTextStyle.Builder().right();
   }

   public static LabelTextStyle.Builder align(TextAlign.ITextAlign textAlign) {
      return new LabelTextStyle.Builder().align(textAlign);
   }

   public int calculateLines(Component component, int width) {
      return this.textWrap.process(component, width).size();
   }

   public int getLabelHeight(Component component, int width) {
      return this.calculateLines(component, width) * 9;
   }

   public static class Builder {
      private TextBorder.ITextBorder border = TextBorder.none();
      private TextWrap.ITextWrap wrap = TextWrap.overflow();
      private TextAlign.ITextAlign align = TextAlign.LEFT;

      public LabelTextStyle.Builder shadow() {
         return this.border(TextBorder.shadow());
      }

      public LabelTextStyle.Builder shadow(TextColor color) {
         return this.border(TextBorder.shadow().color(color));
      }

      public LabelTextStyle.Builder shadow(ChatFormatting color) {
         return this.border(TextBorder.shadow().color(TextColor.fromLegacyFormat(color)));
      }

      public LabelTextStyle.Builder border4() {
         return this.border(TextBorder.border4());
      }

      public LabelTextStyle.Builder border4(TextColor color) {
         return this.border(TextBorder.border4().color(color));
      }

      public LabelTextStyle.Builder border4(ChatFormatting color) {
         return this.border(TextBorder.border4().color(TextColor.fromLegacyFormat(color)));
      }

      public LabelTextStyle.Builder border8() {
         return this.border(TextBorder.border8());
      }

      public LabelTextStyle.Builder border8(TextColor color) {
         return this.border(TextBorder.border8().color(color));
      }

      public LabelTextStyle.Builder border8(ChatFormatting color) {
         return this.border(TextBorder.border8().color(TextColor.fromLegacyFormat(color)));
      }

      public LabelTextStyle.Builder border(TextBorder.ITextBorder border) {
         this.border = border;
         return this;
      }

      public LabelTextStyle.Builder wrap() {
         return this.wrap(TextWrap.wrap());
      }

      public LabelTextStyle.Builder wrap(TextWrap.ITextWrap wrap) {
         this.wrap = wrap;
         return this;
      }

      public LabelTextStyle.Builder left() {
         return this.align(TextAlign.LEFT);
      }

      public LabelTextStyle.Builder center() {
         return this.align(TextAlign.CENTER);
      }

      public LabelTextStyle.Builder right() {
         return this.align(TextAlign.RIGHT);
      }

      public LabelTextStyle.Builder align(TextAlign.ITextAlign align) {
         this.align = align;
         return this;
      }

      public LabelTextStyle build() {
         return new LabelTextStyle(this.border, this.wrap, this.align);
      }
   }
}
