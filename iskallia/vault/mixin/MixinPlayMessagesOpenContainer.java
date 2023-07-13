package iskallia.vault.mixin;

import net.minecraftforge.network.PlayMessages.OpenContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({OpenContainer.class})
public class MixinPlayMessagesOpenContainer {
   @ModifyConstant(
      method = {"decode"},
      constant = {@Constant(
         intValue = 32600
      )},
      remap = false
   )
   private static int bufferLimit(int constant) {
      return Integer.MAX_VALUE;
   }
}
