package iskallia.vault.item.catalyst;

import java.util.function.Function;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public enum ModifierRollType {
   ADD_SPECIFIC_MODIFIER(Function.identity()),
   ADD_RANDOM_MODIFIER(cmp -> new StringTextComponent("A random ").func_230529_a_(cmp).func_240702_b_(" Modifier"));

   private final Function<ITextComponent, ITextComponent> formatter;

   private ModifierRollType(Function<ITextComponent, ITextComponent> formatter) {
      this.formatter = formatter;
   }

   public ITextComponent getDescription(ITextComponent name) {
      return this.formatter.apply(name);
   }
}
