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
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class SoulShardConfig extends Config {
   @Expose
   private int shardTradePrice;
   @Expose
   private final WeightedList<SoulShardConfig.ShardTrade> shardTrades = new WeightedList<>();
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
      this.shardTradePrice = 1000;
      this.shardTrades.clear();
      this.shardTrades.add(new SoulShardConfig.ShardTrade(new ItemEntry(ModItems.SKILL_ESSENCE, 1), 1500, 2500), 1);
      this.shardTrades.add(new SoulShardConfig.ShardTrade(new ItemEntry(ModItems.KNOWLEDGE_STAR_ESSENCE, 1), 900, 1200), 1);
      this.defaultShardDrops = new SoulShardConfig.DropRange(1, 1, 1.0F);
      this.shardDrops.clear();
      this.shardDrops.put(EntityPredicate.of("minecraft:zombie", true).orElseThrow(), new SoulShardConfig.DropRange(1, 1, 0.5F));
   }

   public int getShardTradePrice() {
      return this.shardTradePrice;
   }

   public SoulShardConfig.ShardTrade getRandomTrade() {
      return this.shardTrades.getRandom(rand);
   }

   public int getRandomShards(Entity entity, float chanceMultiplier) {
      for (Entry<EntityPredicate, SoulShardConfig.DropRange> entry : this.shardDrops.entrySet()) {
         if (entry.getKey().test(entity)) {
            return entry.getValue().getRandomAmount(chanceMultiplier);
         }
      }

      return this.defaultShardDrops.getRandomAmount(chanceMultiplier);
   }

   public WeightedList<SoulShardConfig.ShardTrade> getShardTrades() {
      return this.shardTrades;
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
      ModNetwork.CHANNEL.sendTo(new ShardGlobalTradeMessage(cfg.getShardTrades()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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
}
