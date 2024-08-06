package iskallia.vault.mixin;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ConnectionProtocol.PacketSet;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PacketSet.class})
public class MixinPacketSet {
   @Shadow
   @Final
   private static Logger LOGGER;
   @Shadow
   @Final
   private List<Function<FriendlyByteBuf, Packet<PacketListener>>> idToDeserializer;
   @Shadow
   @Final
   Object2IntMap<Class<? extends Packet<PacketListener>>> classToId;

   @Inject(
      method = {"addPacket"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void replaceServerboundCustomPayloadSizeWithUnlimited(
      Class<Packet<PacketListener>> pPacketClass,
      Function<FriendlyByteBuf, Packet<PacketListener>> pDeserializer,
      CallbackInfoReturnable<PacketSet<PacketListener>> cir
   ) {
      if (pPacketClass.equals(ServerboundCustomPayloadPacket.class)) {
         pDeserializer = pBuffer -> {
            ResourceLocation identifier = pBuffer.readResourceLocation();
            int ix = pBuffer.readableBytes();
            FriendlyByteBuf data = new FriendlyByteBuf(pBuffer.readBytes(ix));
            return (Packet<PacketListener>)(new ServerboundCustomPayloadPacket(identifier, data));
         };
         int i = this.idToDeserializer.size();
         int j = this.classToId.put(pPacketClass, i);
         if (j != -1) {
            String s = "Packet " + pPacketClass + " is already registered to ID " + j;
            LOGGER.error(LogUtils.FATAL_MARKER, s);
            throw new IllegalArgumentException(s);
         }

         this.idToDeserializer.add(pDeserializer);
         cir.setReturnValue((PacketSet)this);
      }
   }
}
