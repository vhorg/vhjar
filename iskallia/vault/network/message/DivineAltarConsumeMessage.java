package iskallia.vault.network.message;

import iskallia.vault.block.entity.DivineAltarTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class DivineAltarConsumeMessage {
   private final BlockPos scavengerAltarPos;
   private final int col;

   public DivineAltarConsumeMessage(BlockPos scavengerAltarPos, int col) {
      this.scavengerAltarPos = scavengerAltarPos;
      this.col = col;
   }

   public static void encode(DivineAltarConsumeMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.scavengerAltarPos);
      buffer.writeInt(message.col);
   }

   public static DivineAltarConsumeMessage decode(FriendlyByteBuf buffer) {
      return new DivineAltarConsumeMessage(buffer.readBlockPos(), buffer.readInt());
   }

   public static void handle(DivineAltarConsumeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            DivineAltarTileEntity.spawnConsumeParticles(message.scavengerAltarPos, message.col);
         }
      });
      context.setPacketHandled(true);
   }
}
