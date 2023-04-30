package iskallia.vault.world.data;

import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ShardTradeMessage;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.NetcodeUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerBlackMarketData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerBlackMarket";
   private final Map<UUID, PlayerBlackMarketData.BlackMarket> playerMap = new HashMap<>();
   private static final String TAG_PLAYER_LIST = "playerList";
   private static final String TAG_BLACK_MARKET_LIST = "blackMarketList";

   public static PlayerBlackMarketData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static PlayerBlackMarketData get(MinecraftServer server) {
      return (PlayerBlackMarketData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerBlackMarketData::create, PlayerBlackMarketData::new, "the_vault_PlayerBlackMarket");
   }

   private static PlayerBlackMarketData create(CompoundTag compoundTag) {
      return new PlayerBlackMarketData(compoundTag);
   }

   private PlayerBlackMarketData() {
   }

   private PlayerBlackMarketData(CompoundTag compoundTag) {
      this();
      this.load(compoundTag);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.side.isServer() && event.phase == Phase.START && event.player instanceof ServerPlayer player) {
         get(player.server).getBlackMarket(player).tick(player.server, player);
      }
   }

   public Map<UUID, PlayerBlackMarketData.BlackMarket> getPlayerMap() {
      return this.playerMap;
   }

   public PlayerBlackMarketData.BlackMarket getBlackMarket(Player player) {
      return this.getBlackMarket(player.getUUID());
   }

   public PlayerBlackMarketData.BlackMarket getBlackMarket(UUID playerUuid) {
      return this.playerMap.computeIfAbsent(playerUuid, x$0 -> new PlayerBlackMarketData.BlackMarket(x$0));
   }

   public PlayerBlackMarketData set(ServerPlayer player, Long seed) {
      this.getBlackMarket(player).seed = seed;
      this.setDirty();
      return this;
   }

   private void load(CompoundTag compoundTag) {
      ListTag playerList = compoundTag.getList("playerList", 8);
      ListTag blackMarketList = compoundTag.getList("blackMarketList", 10);
      if (playerList.size() != blackMarketList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getBlackMarket(playerUUID).deserializeNBT(blackMarketList.getCompound(i));
         }
      }
   }

   @Nonnull
   public CompoundTag save(CompoundTag compoundTag) {
      ListTag playerList = new ListTag();
      ListTag blackMarketList = new ListTag();
      this.playerMap.forEach((key, value) -> {
         playerList.add(StringTag.valueOf(key.toString()));
         blackMarketList.add(value.serializeNBT());
      });
      compoundTag.put("playerList", playerList);
      compoundTag.put("blackMarketList", blackMarketList);
      return compoundTag;
   }

   public class BlackMarket {
      private static final Random rand = new Random();
      private LocalDateTime nextReset;
      private long seed;
      private UUID playerUuid;
      private final Map<Integer, PlayerBlackMarketData.BlackMarket.SelectedTrade> trades = new HashMap<>();

      public BlackMarket(UUID playerUuid) {
         this.playerUuid = playerUuid;
         this.seed = rand.nextLong();
         this.resetTrades();
         this.setNextReset();
      }

      public void setNextReset() {
         this.nextReset = LocalDateTime.now(ZoneId.of("UTC"))
            .plusHours(ModConfigs.BLACK_MARKET.getResetHours())
            .plusMinutes(ModConfigs.BLACK_MARKET.getResetMinutes());
      }

      public void resetTrades() {
         this.trades.clear();

         for (int i = 0; i < 3; i++) {
            this.trades.put(i, new PlayerBlackMarketData.BlackMarket.SelectedTrade(ModConfigs.SOUL_SHARD.getRandomTrade()));
         }

         this.setNextReset();
         PlayerBlackMarketData.this.setDirty();
         this.syncToClient(ServerLifecycleHooks.getCurrentServer());
      }

      public boolean useTrade(int tradeId) {
         PlayerBlackMarketData.BlackMarket.SelectedTrade trade = this.trades.get(tradeId);
         if (trade != null && trade.isInfinite) {
            return true;
         } else {
            if (!this.trades.get(tradeId).isInfinite()) {
               this.trades.remove(tradeId);
            }

            PlayerBlackMarketData.this.setDirty();
            return true;
         }
      }

      public void tick(MinecraftServer server, ServerPlayer player) {
         LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
         LocalDateTime end = PlayerBlackMarketData.get(server.overworld()).getBlackMarket(player).nextReset;
         if (end.isBefore(now)) {
            this.resetTrades();
         }
      }

      public Map<Integer, PlayerBlackMarketData.BlackMarket.SelectedTrade> getTrades() {
         return Collections.unmodifiableMap(this.trades);
      }

      public long getSeed() {
         return this.seed;
      }

      public void openTradeContainer(ServerPlayer player) {
         NetworkHooks.openGui(player, new MenuProvider() {
            public Component getDisplayName() {
               return new TextComponent("Black Market");
            }

            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerx) {
               return new ShardTradeContainer(windowId, playerInventory);
            }
         });
      }

      private static PlayerBlackMarketData create(CompoundTag tag) {
         PlayerBlackMarketData data = new PlayerBlackMarketData();
         data.load(tag);
         return data;
      }

      public void deserializeNBT(CompoundTag tag) {
         this.trades.clear();
         this.seed = tag.getLong("seed");
         ListTag list = tag.getList("trades", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag tradeTag = list.getCompound(i);
            this.trades.put(tradeTag.getInt("index"), new PlayerBlackMarketData.BlackMarket.SelectedTrade(tradeTag.getCompound("trade")));
         }

         this.nextReset = LocalDateTime.parse(tag.getString("nextReset"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
         this.playerUuid = tag.getUUID("uuid");
      }

      public CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         ListTag list = new ListTag();
         this.trades.forEach((index, trade) -> {
            CompoundTag tradeTag = new CompoundTag();
            tradeTag.putInt("index", index);
            tradeTag.put("trade", trade.serialize());
            list.add(tradeTag);
         });
         tag.put("trades", list);
         tag.putUUID("uuid", this.playerUuid);
         tag.putLong("seed", this.seed);
         tag.putString("nextReset", this.nextReset.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
         return tag;
      }

      public void syncToClient(MinecraftServer server) {
         NetcodeUtils.runIfPresent(
            server,
            this.playerUuid,
            player -> ModNetwork.CHANNEL
               .sendTo(
                  new ShardTradeMessage(ModConfigs.SOUL_SHARD.getShardTradePrice(), this.seed, this.getTrades(), this.nextReset),
                  player.connection.connection,
                  NetworkDirection.PLAY_TO_CLIENT
               )
         );
      }

      public static PlayerBlackMarketData get(ServerLevel world) {
         return get(world.getServer());
      }

      public static PlayerBlackMarketData get(MinecraftServer server) {
         return (PlayerBlackMarketData)server.overworld()
            .getDataStorage()
            .computeIfAbsent(PlayerBlackMarketData::create, PlayerBlackMarketData::new, "the_vault_PlayerBlackMarket");
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
}
