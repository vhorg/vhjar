package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.data.WeightedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;

public class SoulShardConfig extends Config {
   @Expose
   private int shardTradePrice;
   @Expose
   private final WeightedList<SoulShardConfig.ShardTrade> shardTrades = new WeightedList<>();
   @Expose
   private SoulShardConfig.DropRange defaultShardDrops;
   @Expose
   private final Map<String, SoulShardConfig.DropRange> shardDrops = new HashMap<>();

   @Override
   public String getName() {
      return "soul_shard";
   }

   @Override
   protected void reset() {
      this.shardTradePrice = 1000;
      this.shardTrades.clear();
      this.shardTrades.add(new SoulShardConfig.ShardTrade(new SingleItemEntry(ModItems.SKILL_ESSENCE), 1500, 2500), 1);
      this.shardTrades.add(new SoulShardConfig.ShardTrade(new SingleItemEntry(ModItems.STAR_ESSENCE), 900, 1200), 1);
      this.defaultShardDrops = new SoulShardConfig.DropRange(1, 1, 1.0F);
      this.shardDrops.clear();
      this.shardDrops.put(EntityType.field_200725_aD.getRegistryName().toString(), new SoulShardConfig.DropRange(1, 1, 0.5F));
   }

   public int getShardTradePrice() {
      return this.shardTradePrice;
   }

   public SoulShardConfig.ShardTrade getRandomTrade(Random random) {
      return this.shardTrades.getRandom(random);
   }

   public int getRandomShards(EntityType<?> type) {
      SoulShardConfig.DropRange range = this.shardDrops.get(type.getRegistryName().toString());
      return range == null ? this.defaultShardDrops.getRandomAmount() : range.getRandomAmount();
   }

   public WeightedList<SoulShardConfig.ShardTrade> getShardTrades() {
      return this.shardTrades;
   }

   public void syncTo(ServerPlayerEntity player) {
      ModNetwork.CHANNEL.sendTo(new ShardGlobalTradeMessage(this.getShardTrades()), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
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

      public int getRandomAmount() {
         return Config.rand.nextFloat() > this.chance ? 0 : MathUtilities.getRandomInt(this.min, this.max + 1);
      }
   }

   public static class ShardTrade {
      @Expose
      private final SingleItemEntry item;
      @Expose
      private final int minPrice;
      @Expose
      private final int maxPrice;

      public ShardTrade(SingleItemEntry item, int minPrice, int maxPrice) {
         this.item = item;
         this.minPrice = minPrice;
         this.maxPrice = maxPrice;
      }

      public ItemStack getItem() {
         return this.item.createItemStack();
      }

      public SingleItemEntry getItemEntry() {
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
