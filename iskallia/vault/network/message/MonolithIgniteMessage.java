package iskallia.vault.network.message;

import iskallia.vault.block.entity.MonolithTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class MonolithIgniteMessage {
   private final BlockPos monolithPos;

   public MonolithIgniteMessage(BlockPos monolithPos) {
      this.monolithPos = monolithPos;
   }

   public static void encode(MonolithIgniteMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.monolithPos);
   }

   public static MonolithIgniteMessage decode(FriendlyByteBuf buffer) {
      return new MonolithIgniteMessage(buffer.readBlockPos());
   }

   public static void handle(MonolithIgniteMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            MonolithTileEntity.spawnIgniteParticles(message.monolithPos);
         }
      });
      context.setPacketHandled(true);
   }
}
