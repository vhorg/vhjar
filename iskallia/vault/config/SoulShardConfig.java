package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.data.WeightedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class SoulShardConfig extends Config {
   @Expose
   private Set<SoulShardConfig.Trades> trades;
   @Expose
   private SoulShardConfig.DropRange defaultShardDrops;
   @Expose
   private final Map<EntityPredicate, SoulShardConfig.DropRange> shardDrops = new HashMap<>();

   @Override
   public String getName() {
      return "soul_shard";
   }

   @Override
   protected void reset() {
      this.trades = new HashSet<>();
      WeightedList<SoulShardConfig.ShardTrade> shardTrades = new WeightedList<>();
      shardTrades.clear();
      shardTrades.add(new SoulShardConfig.ShardTrade(new ItemEntry(ModItems.SKILL_ESSENCE, 1), 1500, 2500), 1);
      shardTrades.add(new SoulShardConfig.ShardTrade(new ItemEntry(ModItems.KNOWLEDGE_STAR_ESSENCE, 1), 900, 1200), 1);
      this.trades.add(new SoulShardConfig.Trades(0, shardTrades, 1000));
      this.defaultShardDrops = new SoulShardConfig.DropRange(1, 1, 1.0F);
      this.shardDrops.clear();
      this.shardDrops.put(EntityPredicate.of("minecraft:zombie", true).orElseThrow(), new SoulShardConfig.DropRange(1, 1, 0.5F));
   }

   public Set<SoulShardConfig.Trades> getTrades() {
      return this.trades;
   }

   public int getRandomShards(Entity entity, float chanceMultiplier) {
      for (Entry<EntityPredicate, SoulShardConfig.DropRange> entry : this.shardDrops.entrySet()) {
         if (entry.getKey().test(entity)) {
            return entry.getValue().getRandomAmount(chanceMultiplier);
         }
      }

      return this.defaultShardDrops.getRandomAmount(chanceMultiplier);
   }

   public SoulShardConfig.DropRange getDropRange(Entity entity) {
      for (Entry<EntityPredicate, SoulShardConfig.DropRange> entry : this.shardDrops.entrySet()) {
         if (entry.getKey().test(entity)) {
            return entry.getValue();
         }
      }

      return new SoulShardConfig.DropRange(0, 0, 0.0F);
   }

   @Override
   public <T extends Config> T readConfig() {
      T cfg = super.readConfig();
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      if (srv != null) {
         srv.getPlayerList().getPlayers().forEach(player -> this.syncTo((SoulShardConfig)cfg, player));
      }

      return cfg;
   }

   public void syncTo(SoulShardConfig cfg, ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(new ShardGlobalTradeMessage(cfg.trades), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static class DropRange {
      @Expose
      private final int min;
      @Expose
      private final int max;
      @Expose
      private final float chance;

      public DropRange(int min, int max, float chance) {
         this.min = min;
         this.max = max;
         this.chance = chance;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public float getChance() {
         return this.chance;
      }

      public int getRandomAmount(float chanceMultiplier) {
         int amount = 0;

         for (float chance = this.chance * chanceMultiplier; chance > 0.0F; chance--) {
            if (Config.rand.nextFloat() < chance) {
               amount += MathUtilities.getRandomInt(this.min, this.max + 1);
            }
         }

         return amount;
      }
   }

   public static class ShardTrade {
      @Expose
      private final ItemEntry item;
      @Expose
      private final int minPrice;
      @Expose
      private final int maxPrice;

      public ShardTrade(ItemEntry item, int minPrice, int maxPrice) {
         this.item = item;
         this.minPrice = minPrice;
         this.maxPrice = maxPrice;
      }

      public ItemStack getItem() {
         return this.item.createItemStack();
      }

      public ItemEntry getItemEntry() {
         return this.item;
      }

      public int getMinPrice() {
         return this.minPrice;
      }

      public int getMaxPrice() {
         return this.maxPrice;
      }
   }

   public static class Trades {
      @Expose
      private int minLevel;
      @Expose
      private int shardTradePrice;
      @Expose
      private WeightedList<SoulShardConfig.ShardTrade> shardTrades = new WeightedList<>();

      public Trades(int minLevel, WeightedList<SoulShardConfig.ShardTrade> shardTrades, int shardTradePrice) {
         this.minLevel = minLevel;
         this.shardTrades = shardTrades;
         this.shardTradePrice = shardTradePrice;
      }

      public int getShardTradePrice() {
         return this.shardTradePrice;
      }

      public int getMinLevel() {
         return this.minLevel;
      }

      public SoulShardConfig.ShardTrade getRandomTrade() {
         return this.shardTrades.getRandom(Config.rand);
      }

      public WeightedList<SoulShardConfig.ShardTrade> getShardTrades() {
         return this.shardTrades;
      }
   }
}
