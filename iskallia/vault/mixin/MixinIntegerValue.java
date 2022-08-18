package iskallia.vault.mixin;

import net.minecraft.world.GameRules.IntegerValue;
import net.minecraft.world.GameRules.RuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({IntegerValue.class})
public interface MixinIntegerValue {
   @Invoker("create")
   static RuleType<IntegerValue> create(int defaultValue) {
      throw new AssertionError();
   }
}
