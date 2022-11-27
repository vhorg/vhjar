package iskallia.vault.mixin;

import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ClientboundCustomQueryPacket.class})
public class MixinSCustomPayloadLoginPacket {
   @ModifyConstant(
      method = {"<init>(Lnet/minecraft/network/FriendlyByteBuf;)V"},
      constant = {@Constant(
         intValue = 1048576
      )},
      require = 1
   )
   public int adjustMaxPayloadSize(int maxPayloadSize) {
      return Integer.MAX_VALUE;
   }
}
