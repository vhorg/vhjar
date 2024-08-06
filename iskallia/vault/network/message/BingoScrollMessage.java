package iskallia.vault.network.message;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record BingoScrollMessage(double delta) {
   public static void encode(BingoScrollMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.delta);
   }

   public static BingoScrollMessage decode(FriendlyByteBuf buffer) {
      return new BingoScrollMessage(buffer.readDouble());
   }

   public static void handle(BingoScrollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null && player.getLevel() instanceof VirtualWorld world) {
            ServerVaults.get(world).ifPresent(vault -> vault.get(Vault.OBJECTIVES).forEach(BingoObjective.class, objective -> {
               objective.onScroll(player, message.delta);
               return false;
            }));
         }
      });
      context.setPacketHandled(true);
   }
}
