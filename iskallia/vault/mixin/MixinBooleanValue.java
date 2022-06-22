package iskallia.vault.mixin;

import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.GameRules.RuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({BooleanValue.class})
public interface MixinBooleanValue {
   @Invoker("create")
   static RuleType<BooleanValue> create(boolean defaultValue) {
      throw new AssertionError();
   }
}
