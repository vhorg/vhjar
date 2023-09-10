package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultDiffuserUpgradedTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class DiffuserUpgradedParticleMessage {
   private final BlockPos diffuser;

   public DiffuserUpgradedParticleMessage(BlockPos diffuser) {
      this.diffuser = diffuser;
   }

   public static void encode(DiffuserUpgradedParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.diffuser);
   }

   public static DiffuserUpgradedParticleMessage decode(FriendlyByteBuf buffer) {
      return new DiffuserUpgradedParticleMessage(buffer.readBlockPos());
   }

   public static void handle(DiffuserUpgradedParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            VaultDiffuserUpgradedTileEntity.spawnDiffuserParticles(message.diffuser);
         }
      });
      context.setPacketHandled(true);
   }
}
