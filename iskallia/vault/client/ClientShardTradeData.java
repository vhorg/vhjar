package iskallia.vault.client;

import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.network.message.OmegaShardGlobalTradeMessage;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import iskallia.vault.network.message.ShardTradeMessage;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public class ClientShardTradeData {
   private static int rerollsUsed;
   private static int randomTradeCost;
   private static long tradeSeed;
   private static LocalDateTime nextReset;
   private static Map<Integer, Tuple<ItemStack, Integer>> availableTrades = new HashMap<>();
   private static Set<SoulShardConfig.Trades> shardTrades = new HashSet<>();
   private static Set<OmegaSoulShardConfig.Trades> omegaShardTrades = new HashSet<>();

   public static void receive(ShardTradeMessage message) {
      rerollsUsed = message.getRerollsUsed();
      randomTradeCost = message.getRandomTradeCost();
      tradeSeed = message.getTradeSeed();
      availableTrades = message.getAvailableTrades();
      nextReset = message.getNextReset();
   }

   public static void receiveGlobal(ShardGlobalTradeMessage message) {
      shardTrades = message.getShardTrades();
   }

   public static void receiveGlobal(OmegaShardGlobalTradeMessage message) {
      omegaShardTrades = message.getShardTrades();
   }

   public static int getRerollsUsed() {
      return rerollsUsed;
   }

   public static int getRandomTradeCost() {
      return randomTradeCost;
   }

   public static long getTradeSeed() {
      return tradeSeed;
   }

   public static LocalDateTime getNextReset() {
      return nextReset;
   }

   public static void nextSeed() {
      Random r = new Random(tradeSeed);

      for (int i = 0; i < 3; i++) {
         r.nextLong();
      }

      tradeSeed = r.nextLong();
   }

   public static Map<Integer, Tuple<ItemStack, Integer>> getAvailableTrades() {
      return Collections.unmodifiableMap(availableTrades);
   }

   public static Tuple<ItemStack, Integer> getTradeInfo(int trade) {
      return availableTrades.get(trade);
   }
}
