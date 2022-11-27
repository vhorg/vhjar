package iskallia.vault.mixin;

import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({IntegerValue.class})
public interface MixinIntegerValue {
   @Invoker("create")
   static Type<IntegerValue> create(int defaultValue) {
      throw new AssertionError();
   }
}
