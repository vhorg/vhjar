package iskallia.vault.network.message;

import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ShardGlobalTradeMessage {
   private final WeightedList<SoulShardConfig.ShardTrade> shardTrades;

   public ShardGlobalTradeMessage(WeightedList<SoulShardConfig.ShardTrade> shardTrades) {
      this.shardTrades = shardTrades;
   }

   public WeightedList<SoulShardConfig.ShardTrade> getShardTrades() {
      return this.shardTrades;
   }

   public static void encode(ShardGlobalTradeMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.shardTrades.size());
      message.shardTrades.forEach((trade, nbr) -> {
         SingleItemEntry entry = trade.getItemEntry();
         buffer.func_180714_a(entry.ITEM);
         buffer.writeBoolean(entry.NBT != null);
         if (entry.NBT != null) {
            buffer.func_180714_a(trade.getItemEntry().NBT);
         }

         buffer.writeInt(trade.getMinPrice());
         buffer.writeInt(trade.getMaxPrice());
         buffer.writeInt(nbr.intValue());
      });
   }

   public static ShardGlobalTradeMessage decode(PacketBuffer buffer) {
      WeightedList<SoulShardConfig.ShardTrade> trades = new WeightedList<>();
      int tradeCount = buffer.readInt();

      for (int i = 0; i < tradeCount; i++) {
         String item = buffer.func_150789_c(32767);
         String nbt = null;
         if (buffer.readBoolean()) {
            nbt = buffer.func_150789_c(32767);
         }

         int min = buffer.readInt();
         int max = buffer.readInt();
         int weight = buffer.readInt();
         SoulShardConfig.ShardTrade trade = new SoulShardConfig.ShardTrade(new SingleItemEntry(item, nbt), min, max);
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
