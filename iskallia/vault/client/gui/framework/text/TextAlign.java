package iskallia.vault.client.gui.framework.text;

public final class TextAlign {
   public static final TextAlign.ITextAlign LEFT = (x, textWidth, maxWidth) -> x;
   public static final TextAlign.ITextAlign CENTER = (x, textWidth, maxWidth) -> x + (maxWidth - textWidth) / 2;
   public static final TextAlign.ITextAlign RIGHT = (x, textWidth, maxWidth) -> x + maxWidth - textWidth;

   private TextAlign() {
   }

   @FunctionalInterface
   public interface ITextAlign {
      int calculateX(int var1, int var2, int var3);
   }
}
