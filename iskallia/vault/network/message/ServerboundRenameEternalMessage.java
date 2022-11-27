package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.init.ModNetwork;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundRenameEternalMessage {
   private final BlockPos pos;

   private ServerboundRenameEternalMessage(BlockPos pos) {
      this.pos = pos;
   }

   public static void send(BlockPos pos) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundRenameEternalMessage(pos));
   }

   public static void encode(ServerboundRenameEternalMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
   }

   public static ServerboundRenameEternalMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      return new ServerboundRenameEternalMessage(pos);
   }

   public static void handle(ServerboundRenameEternalMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         if (serverPlayer != null) {
            if (serverPlayer.getLevel().getBlockEntity(message.pos) instanceof CryoChamberTileEntity cryoChamberTileEntity) {
               cryoChamberTileEntity.renameEternal(serverPlayer);
            } else if (serverPlayer.getLevel().getBlockEntity(message.pos.below()) instanceof CryoChamberTileEntity cryoChamberTileEntity) {
               cryoChamberTileEntity.renameEternal(serverPlayer);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
