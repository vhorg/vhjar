package iskallia.vault.network.message;

import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ShardGlobalTradeMessage {
   private final Set<SoulShardConfig.Trades> trades;

   public ShardGlobalTradeMessage(Set<SoulShardConfig.Trades> trades) {
      this.trades = trades;
   }

   public Set<SoulShardConfig.Trades> getShardTrades() {
      return this.trades;
   }

   public static void encode(ShardGlobalTradeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.trades.size());
      message.trades.forEach(trades -> {
         buffer.writeInt(trades.getMinLevel());
         buffer.writeInt(trades.getShardTradePrice());
         buffer.writeInt(trades.getShardTrades().size());
         trades.getShardTrades().forEach((trade, nbr) -> {
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
      });
   }

   public static ShardGlobalTradeMessage decode(FriendlyByteBuf buffer) {
      Set<SoulShardConfig.Trades> tradesList = new HashSet<>();
      int tradesSize = buffer.readInt();

      for (int i = 0; i < tradesSize; i++) {
         WeightedList<SoulShardConfig.ShardTrade> trades = new WeightedList<>();
         int minLevel = buffer.readInt();
         int shardTradePrice = buffer.readInt();
         int tradeCount = buffer.readInt();

         for (int j = 0; j < tradeCount; j++) {
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

         tradesList.add(new SoulShardConfig.Trades(minLevel, trades, shardTradePrice));
      }

      return new ShardGlobalTradeMessage(tradesList);
   }

   public static void handle(ShardGlobalTradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientShardTradeData.receiveGlobal(message));
      context.setPacketHandled(true);
   }
}
