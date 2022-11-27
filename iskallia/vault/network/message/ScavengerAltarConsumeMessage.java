package iskallia.vault.network.message;

import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ScavengerAltarConsumeMessage {
   private final BlockPos scavengerAltarPos;

   public ScavengerAltarConsumeMessage(BlockPos scavengerAltarPos) {
      this.scavengerAltarPos = scavengerAltarPos;
   }

   public static void encode(ScavengerAltarConsumeMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.scavengerAltarPos);
   }

   public static ScavengerAltarConsumeMessage decode(FriendlyByteBuf buffer) {
      return new ScavengerAltarConsumeMessage(buffer.readBlockPos());
   }

   public static void handle(ScavengerAltarConsumeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            ScavengerAltarTileEntity.spawnConsumeParticles(message.scavengerAltarPos);
         }
      });
      context.setPacketHandled(true);
   }
}
