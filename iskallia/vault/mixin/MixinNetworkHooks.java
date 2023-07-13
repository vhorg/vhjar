package iskallia.vault.mixin;

import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({NetworkHooks.class})
public class MixinNetworkHooks {
   @ModifyConstant(
      method = {"openGui(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)V"},
      constant = {@Constant(
         intValue = 32600
      )},
      remap = false
   )
   private static int bufferLimit(int constant) {
      return Integer.MAX_VALUE;
   }
}
