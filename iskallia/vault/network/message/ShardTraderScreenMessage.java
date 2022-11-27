package iskallia.vault.network.message;

import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.SoulShardTraderData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

public class ShardTraderScreenMessage {
   public static void encode(ShardTraderScreenMessage message, FriendlyByteBuf buffer) {
   }

   public static ShardTraderScreenMessage decode(FriendlyByteBuf buffer) {
      return new ShardTraderScreenMessage();
   }

   public static void handle(ShardTraderScreenMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerLevel sWorld = context.getSender().getLevel();
         if (!ServerVaults.isVaultWorld(sWorld) && !VHSmpUtil.isArenaWorld(sWorld)) {
            SoulShardTraderData.get(sWorld).openTradeContainer(context.getSender());
         }
      });
      context.setPacketHandled(true);
   }
}
