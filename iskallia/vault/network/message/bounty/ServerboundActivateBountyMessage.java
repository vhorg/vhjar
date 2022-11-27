package iskallia.vault.network.message.bounty;

import iskallia.vault.container.BountyContainer;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundActivateBountyMessage(UUID bountyId) {
   public static void encode(ServerboundActivateBountyMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.bountyId);
   }

   public static ServerboundActivateBountyMessage decode(FriendlyByteBuf buffer) {
      UUID bountyId = buffer.readUUID();
      return new ServerboundActivateBountyMessage(bountyId);
   }

   public static void handle(ServerboundActivateBountyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         if (serverPlayer != null) {
            BountyData.get().setActive(serverPlayer.getUUID(), message.bountyId);
            if (serverPlayer.containerMenu instanceof BountyContainer container) {
               container.replaceActive(BountyData.get().getAllActiveFor(serverPlayer.getUUID()));
               container.broadcastChanges();
            }
         }
      });
      context.setPacketHandled(true);
   }
}
