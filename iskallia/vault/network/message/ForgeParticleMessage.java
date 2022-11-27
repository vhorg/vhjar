package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ForgeParticleMessage {
   private final BlockPos recycler;

   public ForgeParticleMessage(BlockPos recycler) {
      this.recycler = recycler;
   }

   public static void encode(ForgeParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.recycler);
   }

   public static ForgeParticleMessage decode(FriendlyByteBuf buffer) {
      return new ForgeParticleMessage(buffer.readBlockPos());
   }

   public static void handle(ForgeParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            VaultForgeTileEntity.spawnForgeParticles(message.recycler);
         }
      });
      context.setPacketHandled(true);
   }
}
