package iskallia.vault.attribute;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public abstract class VModifier<T, I extends VAttribute.Instance<T>> extends VAttribute<T, I> {
   public VModifier(ResourceLocation id, Supplier<I> instance) {
      super(id, instance);
   }

   @Override
   protected String getTagKey() {
      return "Modifiers";
   }

   public abstract T apply(VAttribute.Instance<T> var1, T var2);
}
