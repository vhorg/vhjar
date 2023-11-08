package iskallia.vault.network.message;

import iskallia.vault.block.entity.AlchemyArchiveTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class AlchemyArchiveDiscoverEffectMessage {
   private final BlockPos pos;
   private final String effectId;

   public AlchemyArchiveDiscoverEffectMessage(BlockPos pos, String effectId) {
      this.pos = pos;
      this.effectId = effectId;
   }

   public static void encode(AlchemyArchiveDiscoverEffectMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeUtf(message.effectId);
   }

   public static AlchemyArchiveDiscoverEffectMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      String effectId = buffer.readUtf();
      return new AlchemyArchiveDiscoverEffectMessage(pos, effectId);
   }

   public static void handle(AlchemyArchiveDiscoverEffectMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         BlockPos pos = message.pos;
         if (player.getLevel().getBlockEntity(pos) instanceof AlchemyArchiveTileEntity alchemyTableTile) {
            alchemyTableTile.discoverEffect(player, message.effectId);
         }
      });
      context.setPacketHandled(true);
   }
}
