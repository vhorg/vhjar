package iskallia.vault.network.message;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.container.modifier.DiscoverableModifier;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class DiscoverModifierArchiveMessage {
   private final BlockPos pos;
   private final DiscoverableModifier gearModifier;

   public DiscoverModifierArchiveMessage(BlockPos pos, DiscoverableModifier gearModifier) {
      this.pos = pos;
      this.gearModifier = gearModifier;
   }

   public static void encode(DiscoverModifierArchiveMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      message.gearModifier.serialize(buffer);
   }

   public static DiscoverModifierArchiveMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      return new DiscoverModifierArchiveMessage(pos, DiscoverableModifier.deserialize(buffer));
   }

   public static void handle(DiscoverModifierArchiveMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         BlockPos pos = message.pos;
         if (player.getLevel().getBlockEntity(pos) instanceof ModifierDiscoveryTileEntity modifierDiscoveryTile) {
            modifierDiscoveryTile.discoverModifierOnTile(player, message.gearModifier);
         }
      });
      context.setPacketHandled(true);
   }
}
