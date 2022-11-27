package iskallia.vault.mixin;

import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ClientboundCustomPayloadPacket.class})
public class MixinSCustomPayloadPlayPacket {
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

   @ModifyConstant(
      method = {"<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/FriendlyByteBuf;)V"},
      constant = {@Constant(
         intValue = 1048576
      )},
      require = 1
   )
   public int adjustCtorMaxPayloadSize(int maxPayloadSize) {
      return Integer.MAX_VALUE;
   }
}
