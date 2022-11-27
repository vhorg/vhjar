package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.network.message.ShardTradeMessage;
import iskallia.vault.util.MathUtilities;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class SoulShardTraderData extends SavedData {
   protected static final String DATA_NAME = "the_vault_SoulShardTrader";
   private static final Random rand = new Random();
   private long nextReset = 0L;
   private long seed = 0L;
   private final Map<Integer, SoulShardTraderData.SelectedTrade> trades = new HashMap<>();

   public void resetDailyTrades() {
      this.resetTrades();
      VaultMod.LOGGER.info("Reset SoulShard Trades!");
   }

   public void resetTrades() {
      this.trades.clear();

      for (int i = 0; i < 3; i++) {
         this.trades.put(i, new SoulShardTraderData.SelectedTrade(ModConfigs.SOUL_SHARD.getRandomTrade()));
      }

      if (ModConfigs.RAID_EVENT_CONFIG.isEnabled()) {
         ItemStack eventSeal = new ItemStack(ModItems.CRYSTAL_SEAL_RAID);
         ItemVaultCrystalSeal.setEventKey(eventSeal, "raid");
         SoulShardTraderData.SelectedTrade eventTrade = new SoulShardTraderData.SelectedTrade(eventSeal, ModConfigs.RAID_EVENT_CONFIG.getSoulShardTradeCost());
         eventTrade.isInfinite = true;
         this.trades.put(0, eventTrade);
      }

      this.nextReset = System.currentTimeMillis() / 1000L + Duration.ofDays(1L).getSeconds();
      this.setDirty();
   }

   public boolean useTrade(int tradeId) {
      SoulShardTraderData.SelectedTrade trade = this.trades.get(tradeId);
      if (trade != null && trade.isInfinite) {
         return true;
      } else {
         this.trades.remove(tradeId);
         this.setDirty();
         return true;
      }
   }

   public Map<Integer, SoulShardTraderData.SelectedTrade> getTrades() {
      return Collections.unmodifiableMap(this.trades);
   }

   public long getSeed() {
      return this.seed;
   }

   public void nextSeed() {
      Random r = new Random(this.seed);

      for (int i = 0; i < 3; i++) {
         r.nextLong();
      }

      this.seed = r.nextLong();
      this.setDirty();
   }

   public void setDirty() {
      super.setDirty();
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), this.getUpdatePacket());
   }

   public ShardTradeMessage getUpdatePacket() {
      return new ShardTradeMessage(ModConfigs.SOUL_SHARD.getShardTradePrice(), this.seed, this.getTrades());
   }

   public void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void openTradeContainer(ServerPlayer player) {
      NetworkHooks.openGui(player, new MenuProvider() {
         public Component getDisplayName() {
            return new TextComponent("Soul Shard Trading");
         }

         public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerx) {
            return new ShardTradeContainer(windowId, playerInventory);
         }
      });
   }

   private static SoulShardTraderData create(CompoundTag tag) {
      SoulShardTraderData data = new SoulShardTraderData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag tag) {
      this.trades.clear();
      this.seed = tag.getLong("seed");
      ListTag list = tag.getList("trades", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag tradeTag = list.getCompound(i);
         this.trades.put(tradeTag.getInt("index"), new SoulShardTraderData.SelectedTrade(tradeTag.getCompound("trade")));
      }

      this.nextReset = tag.getLong("nextReset");
      if (this.nextReset < System.currentTimeMillis() / 1000L) {
         this.seed = rand.nextLong();
         this.resetTrades();
      }
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag list = new ListTag();
      this.trades.forEach((index, trade) -> {
         CompoundTag tradeTag = new CompoundTag();
         tradeTag.putInt("index", index);
         tradeTag.put("trade", trade.serialize());
         list.add(tradeTag);
      });
      tag.put("trades", list);
      tag.putLong("seed", this.seed);
      tag.putLong("nextReset", this.nextReset);
      return tag;
   }

   public static SoulShardTraderData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static SoulShardTraderData get(MinecraftServer server) {
      return (SoulShardTraderData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(SoulShardTraderData::create, SoulShardTraderData::new, "the_vault_SoulShardTrader");
   }

   public static class SelectedTrade {
      private final ItemStack stack;
      private final int shardCost;
      private boolean isInfinite = false;

      public SelectedTrade(SoulShardConfig.ShardTrade trade) {
         this.stack = trade.getItem();
         this.shardCost = MathUtilities.getRandomInt(trade.getMinPrice(), trade.getMaxPrice() + 1);
      }

      public SelectedTrade(ItemStack stack, int shardCost) {
         this.stack = stack;
         this.shardCost = shardCost;
      }

      public SelectedTrade(CompoundTag tag) {
         this.stack = ItemStack.of(tag.getCompound("stack"));
         this.shardCost = tag.getInt("cost");
         this.isInfinite = tag.getBoolean("infinite");
      }

      public int getShardCost() {
         return this.shardCost;
      }

      public ItemStack getStack() {
         return this.stack.copy();
      }

      public boolean isInfinite() {
         return this.isInfinite;
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.put("stack", this.stack.serializeNBT());
         tag.putInt("cost", this.shardCost);
         tag.putBoolean("infinite", this.isInfinite);
         return tag;
      }
   }
}
