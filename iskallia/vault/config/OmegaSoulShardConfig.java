package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.OmegaShardGlobalTradeMessage;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.data.WeightedList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class OmegaSoulShardConfig extends Config {
   @Expose
   private Set<OmegaSoulShardConfig.Trades> trades;

   @Override
   public String getName() {
      return "omega_soul_shard";
   }

   @Override
   protected void reset() {
      this.trades = new HashSet<>();
      WeightedList<OmegaSoulShardConfig.ShardTrade> shardTrades = new WeightedList<>();
      shardTrades.clear();
      shardTrades.add(new OmegaSoulShardConfig.ShardTrade(new ItemEntry(ModItems.KEY_PIECE, 1), 1500, 2500), 1);
      shardTrades.add(new OmegaSoulShardConfig.ShardTrade(new ItemEntry(ModItems.TRINKET, 1), 900, 1200), 1);
      this.trades.add(new OmegaSoulShardConfig.Trades(0, shardTrades));
   }

   public Set<OmegaSoulShardConfig.Trades> getTrades() {
      return this.trades;
   }

   @Override
   public <T extends Config> T readConfig() {
      T cfg = super.readConfig();
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      if (srv != null) {
         srv.getPlayerList().getPlayers().forEach(player -> this.syncTo((OmegaSoulShardConfig)cfg, player));
      }

      return cfg;
   }

   public void syncTo(OmegaSoulShardConfig cfg, ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(new OmegaShardGlobalTradeMessage(cfg.trades), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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

   public static class Trades {
      @Expose
      private int minLevel;
      @Expose
      private WeightedList<OmegaSoulShardConfig.ShardTrade> shardTrades = new WeightedList<>();

      public Trades(int minLevel, WeightedList<OmegaSoulShardConfig.ShardTrade> shardTrades) {
         this.minLevel = minLevel;
         this.shardTrades = shardTrades;
      }

      public int getMinLevel() {
         return this.minLevel;
      }

      public OmegaSoulShardConfig.ShardTrade getRandomTrade() {
         return this.shardTrades.getRandom(Config.rand);
      }

      public WeightedList<OmegaSoulShardConfig.ShardTrade> getShardTrades() {
         return this.shardTrades;
      }
   }
}
