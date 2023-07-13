package iskallia.vault.network.message;

import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class OmegaShardGlobalTradeMessage {
   private final Set<OmegaSoulShardConfig.Trades> trades;

   public OmegaShardGlobalTradeMessage(Set<OmegaSoulShardConfig.Trades> trades) {
      this.trades = trades;
   }

   public Set<OmegaSoulShardConfig.Trades> getShardTrades() {
      return this.trades;
   }

   public static void encode(OmegaShardGlobalTradeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.trades.size());
      message.trades.forEach(trades -> {
         buffer.writeInt(trades.getMinLevel());
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

   public static OmegaShardGlobalTradeMessage decode(FriendlyByteBuf buffer) {
      Set<OmegaSoulShardConfig.Trades> tradesList = new HashSet<>();
      int tradesSize = buffer.readInt();

      for (int i = 0; i < tradesSize; i++) {
         WeightedList<OmegaSoulShardConfig.ShardTrade> trades = new WeightedList<>();
         int minLevel = buffer.readInt();
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
            OmegaSoulShardConfig.ShardTrade trade = new OmegaSoulShardConfig.ShardTrade(new ItemEntry(item, amount, nbt), min, max);
            trades.add(trade, weight);
         }

         tradesList.add(new OmegaSoulShardConfig.Trades(minLevel, trades));
      }

      return new OmegaShardGlobalTradeMessage(tradesList);
   }

   public static void handle(OmegaShardGlobalTradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientShardTradeData.receiveGlobal(message));
      context.setPacketHandled(true);
   }
}
