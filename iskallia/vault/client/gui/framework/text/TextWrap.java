package iskallia.vault.client.gui.framework.text;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public final class TextWrap {
   private static final TextWrap.ITextWrap OVERFLOW = (component, width) -> List.of(component.getVisualOrderText());
   private static final TextWrap.ITextWrap WRAP = (component, width) -> Minecraft.getInstance().font.split(component, width);

   public static TextWrap.ITextWrap overflow() {
      return OVERFLOW;
   }

   public static TextWrap.ITextWrap wrap() {
      return WRAP;
   }

   private TextWrap() {
   }

   @FunctionalInterface
   public interface ITextWrap {
      List<FormattedCharSequence> process(Component var1, int var2);
   }
}
