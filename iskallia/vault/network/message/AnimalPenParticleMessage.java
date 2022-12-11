package iskallia.vault.network.message;

import iskallia.vault.block.entity.AnimalPenTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AnimalPenParticleMessage {
   private final BlockPos animalPen;

   public AnimalPenParticleMessage(BlockPos animalPen) {
      this.animalPen = animalPen;
   }

   public static void encode(AnimalPenParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.animalPen);
   }

   public static AnimalPenParticleMessage decode(FriendlyByteBuf buffer) {
      return new AnimalPenParticleMessage(buffer.readBlockPos());
   }

   public static void handle(AnimalPenParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            AnimalPenTileEntity.spawnParticles(message.animalPen);
         }
      });
      context.setPacketHandled(true);
   }
}
