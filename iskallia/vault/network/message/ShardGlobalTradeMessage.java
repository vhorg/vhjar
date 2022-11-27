package iskallia.vault.network.message;

import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ShardGlobalTradeMessage {
   private final WeightedList<SoulShardConfig.ShardTrade> shardTrades;

   public ShardGlobalTradeMessage(WeightedList<SoulShardConfig.ShardTrade> shardTrades) {
      this.shardTrades = shardTrades;
   }

   public WeightedList<SoulShardConfig.ShardTrade> getShardTrades() {
      return this.shardTrades;
   }

   public static void encode(ShardGlobalTradeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.shardTrades.size());
      message.shardTrades.forEach((trade, nbr) -> {
         ItemEntry entry = trade.getItemEntry();
         buffer.writeUtf(entry.ITEM);
         buffer.writeInt(entry.AMOUNT);
         buffer.writeBoolean(entry.NBT != null);
         if (entry.NBT != null) {
            buffer.writeUtf(trade.getItemEntry().NBT);
         }

         buffer.writeInt(trade.getMinPrice());
         buffer.writeInt(trade.getMaxPrice());
         buffer.writeInt(nbr.intValue());
      });
   }

   public static ShardGlobalTradeMessage decode(FriendlyByteBuf buffer) {
      WeightedList<SoulShardConfig.ShardTrade> trades = new WeightedList<>();
      int tradeCount = buffer.readInt();

      for (int i = 0; i < tradeCount; i++) {
         String item = buffer.readUtf(32767);
         int amount = buffer.readInt();
         String nbt = null;
         if (buffer.readBoolean()) {
            nbt = buffer.readUtf(32767);
         }

         int min = buffer.readInt();
         int max = buffer.readInt();
         int weight = buffer.readInt();
         SoulShardConfig.ShardTrade trade = new SoulShardConfig.ShardTrade(new ItemEntry(item, amount, nbt), min, max);
         trades.add(trade, weight);
      }

      return new ShardGlobalTradeMessage(trades);
   }

   public static void handle(ShardGlobalTradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientShardTradeData.receiveGlobal(message));
      context.setPacketHandled(true);
   }
}
