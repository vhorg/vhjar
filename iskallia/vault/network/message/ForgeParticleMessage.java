package iskallia.vault.network.message;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ForgeParticleMessage {
   private final BlockPos tilePos;

   public ForgeParticleMessage(BlockPos tilePos) {
      this.tilePos = tilePos;
   }

   public static void encode(ForgeParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.tilePos);
   }

   public static ForgeParticleMessage decode(FriendlyByteBuf buffer) {
      return new ForgeParticleMessage(buffer.readBlockPos());
   }

   public static void handle(ForgeParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ForgeRecipeTileEntity.spawnForgeParticles(message.tilePos));
      context.setPacketHandled(true);
   }
}
