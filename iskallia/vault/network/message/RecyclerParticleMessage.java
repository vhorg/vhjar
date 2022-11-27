package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultRecyclerTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class RecyclerParticleMessage {
   private final BlockPos forge;

   public RecyclerParticleMessage(BlockPos forge) {
      this.forge = forge;
   }

   public static void encode(RecyclerParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.forge);
   }

   public static RecyclerParticleMessage decode(FriendlyByteBuf buffer) {
      return new RecyclerParticleMessage(buffer.readBlockPos());
   }

   public static void handle(RecyclerParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            VaultRecyclerTileEntity.spawnRecycleParticles(message.forge);
         }
      });
      context.setPacketHandled(true);
   }
}
