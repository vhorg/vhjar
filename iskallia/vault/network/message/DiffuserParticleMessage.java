package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultDiffuserTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class DiffuserParticleMessage {
   private final BlockPos diffuser;

   public DiffuserParticleMessage(BlockPos diffuser) {
      this.diffuser = diffuser;
   }

   public static void encode(DiffuserParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.diffuser);
   }

   public static DiffuserParticleMessage decode(FriendlyByteBuf buffer) {
      return new DiffuserParticleMessage(buffer.readBlockPos());
   }

   public static void handle(DiffuserParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            VaultDiffuserTileEntity.spawnDiffuserParticles(message.diffuser);
         }
      });
      context.setPacketHandled(true);
   }
}
