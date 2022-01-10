package iskallia.vault.mixin;

import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({SCustomPayloadLoginPacket.class})
public class MixinSCustomPayloadLoginPacket {
   @ModifyConstant(
      method = {"readPacketData"},
      constant = {@Constant(
         intValue = 1048576
      )},
      require = 1
   )
   public int adjustMaxPayloadSize(int maxPayloadSize) {
      return Integer.MAX_VALUE;
   }
}
