package iskallia.vault.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerboundSetStructureBlockPacket.class})
public class MixinCUpdateStructureBlockPacket {
   @Shadow
   @Mutable
   @Final
   private BlockPos offset;
   @Shadow
   @Mutable
   @Final
   private Vec3i size;

   @Inject(
      method = {"<init>(Lnet/minecraft/network/FriendlyByteBuf;)V"},
      at = {@At("RETURN")}
   )
   private void load(FriendlyByteBuf buf, CallbackInfo ci) {
      this.offset = new BlockPos(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
      this.size = new Vec3i(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
   }

   @Inject(
      method = {"write"},
      at = {@At("RETURN")}
   )
   private void writePacketData(FriendlyByteBuf buf, CallbackInfo ci) {
      buf.writeVarInt(this.offset.getX());
      buf.writeVarInt(this.offset.getY());
      buf.writeVarInt(this.offset.getZ());
      buf.writeVarInt(this.size.getX());
      buf.writeVarInt(this.size.getY());
      buf.writeVarInt(this.size.getZ());
   }
}
