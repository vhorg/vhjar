package iskallia.vault.mixin;

import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({BooleanValue.class})
public interface MixinBooleanValue {
   @Invoker("create")
   static Type<BooleanValue> create(boolean defaultValue, BiConsumer<MinecraftServer, BooleanValue> changeListener) {
      throw new AssertionError();
   }
}
