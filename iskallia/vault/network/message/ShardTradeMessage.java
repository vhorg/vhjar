package iskallia.vault.network.message;

import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.world.data.PlayerBlackMarketData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ShardTradeMessage {
   private final int rerollsUsed;
   private final int randomTradeCost;
   private final long seed;
   private final LocalDateTime nextReset;
   private final Map<Integer, Tuple<ItemStack, Integer>> availableTrades = new HashMap<>();

   private ShardTradeMessage(int rerollsUsed, int randomTradeCost, long seed, String nextReset) {
      this.rerollsUsed = rerollsUsed;
      this.randomTradeCost = randomTradeCost;
      this.seed = seed;
      this.nextReset = LocalDateTime.parse(nextReset, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
   }

   public ShardTradeMessage(
      int rerollsUsed, int randomTradeCost, long seed, Map<Integer, PlayerBlackMarketData.BlackMarket.SelectedTrade> trades, LocalDateTime nextReset
   ) {
      this.rerollsUsed = rerollsUsed;
      this.randomTradeCost = randomTradeCost;
      this.seed = seed;
      trades.forEach((index, trade) -> {
         Tuple<ItemStack, Integer> tradeTpl = new Tuple(trade.getStack(), trade.getShardCost());
         this.availableTrades.put(index, tradeTpl);
      });
      this.nextReset = nextReset;
   }

   public int getRerollsUsed() {
      return this.rerollsUsed;
   }

   public int getRandomTradeCost() {
      return this.randomTradeCost;
   }

   public long getTradeSeed() {
      return this.seed;
   }

   public LocalDateTime getNextReset() {
      return this.nextReset;
   }

   public Map<Integer, Tuple<ItemStack, Integer>> getAvailableTrades() {
      return this.availableTrades;
   }

   public static void encode(ShardTradeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.rerollsUsed);
      buffer.writeInt(message.randomTradeCost);
      buffer.writeLong(message.seed);
      buffer.writeUtf(message.nextReset.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      buffer.writeInt(message.availableTrades.size());
      message.availableTrades.forEach((index, tradeTpl) -> {
         buffer.writeInt(index);
         buffer.writeItem((ItemStack)tradeTpl.getA());
         buffer.writeInt((Integer)tradeTpl.getB());
      });
   }

   public static ShardTradeMessage decode(FriendlyByteBuf buffer) {
      ShardTradeMessage message = new ShardTradeMessage(buffer.readInt(), buffer.readInt(), buffer.readLong(), buffer.readUtf());
      int trades = buffer.readInt();

      for (int i = 0; i < trades; i++) {
         int index = buffer.readInt();
         ItemStack tradeStack = buffer.readItem();
         int cost = buffer.readInt();
         message.availableTrades.put(index, new Tuple(tradeStack, cost));
      }

      return message;
   }

   public static void handle(ShardTradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientShardTradeData.receive(message));
      context.setPacketHandled(true);
   }
}
