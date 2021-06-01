package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.Trade;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class GlobalTraderData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_GlobalTrader";
   private static final Map<UUID, List<Trade>> playerMap = new HashMap<>();
   private static final int dayInSeconds = 86400;
   private static int resetCounter = 0;
   private static boolean isReset = false;

   public GlobalTraderData() {
      this("the_vault_GlobalTrader");
   }

   public GlobalTraderData(String name) {
      super(name);
   }

   public List<Trade> getPlayerTrades(PlayerEntity player) {
      return this.getPlayerTrades(player.func_110124_au());
   }

   public List<Trade> getPlayerTrades(UUID uuid) {
      List<Trade> trades = playerMap.computeIfAbsent(uuid, id -> getNewTrades());
      this.func_76185_a();
      return trades;
   }

   public ListNBT getPlayerTradesAsNbt(PlayerEntity player) {
      ListNBT playerTradesList = new ListNBT();

      for (Trade trade : this.getPlayerTrades(player)) {
         try {
            playerTradesList.add(NBTSerializer.serialize(trade));
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      return playerTradesList;
   }

   public void updatePlayerTrades(PlayerEntity playerEntity, List<Trade> trades) {
      playerMap.replace(playerEntity.func_110124_au(), trades);
      this.func_76185_a();
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.phase != Phase.END && event.world.func_234923_W_() == World.field_234918_g_ && !event.side.isClient()) {
         long time = Instant.now().getEpochSecond();
         if (time % 86400L == 0L && !isReset) {
            reset((ServerWorld)event.world);
            isReset = true;
         }

         if (isReset && resetCounter++ >= 600) {
            isReset = false;
            resetCounter = 0;
         }
      }
   }

   public static void reset(ServerWorld world) {
      resetTrades();
      get(world).func_76185_a();
      Vault.LOGGER.info("Global Trades Reset");
   }

   private static void resetTrades() {
      playerMap.clear();
   }

   private static List<Trade> getNewTrades() {
      List<Trade> possibleTrades = new ArrayList<>();
      Random rand = new Random();

      for (int tradeCount = 0; tradeCount < ModConfigs.GLOBAL_TRADER.TOTAL_TRADE_COUNT; tradeCount++) {
         if (tradeCount == 0) {
            Trade trade = ModConfigs.GLOBAL_TRADER.POOL.getRandom(rand).copy();
            trade.setMaxTrades(ModConfigs.GLOBAL_TRADER.MAX_TRADES);
            possibleTrades.add(trade);
         } else {
            Trade potentialTrade = ModConfigs.GLOBAL_TRADER.POOL.getRandom(rand).copy();

            while (possibleTrades.size() < ModConfigs.GLOBAL_TRADER.TOTAL_TRADE_COUNT) {
               if (possibleTrades.contains(potentialTrade)) {
                  potentialTrade = ModConfigs.GLOBAL_TRADER.POOL.getRandom(rand).copy();
               } else {
                  potentialTrade.setMaxTrades(ModConfigs.GLOBAL_TRADER.MAX_TRADES);
                  possibleTrades.add(potentialTrade);
               }
            }
         }
      }

      return possibleTrades;
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerList", 8);
      ListNBT playerTradesList = nbt.func_150295_c("PlayerTradesList", 9);
      if (playerList.size() != playerTradesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         try {
            for (int i = 0; i < playerList.size(); i++) {
               UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
               List<Trade> trades = new ArrayList<>();

               for (INBT tradeData : playerTradesList.func_202169_e(i)) {
                  trades.add(NBTSerializer.deserialize(Trade.class, (CompoundNBT)tradeData));
               }

               playerMap.put(playerUUID, trades);
            }
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT compound) {
      ListNBT playerList = new ListNBT();
      ListNBT playerTradesList = new ListNBT();
      playerMap.forEach((uuid, t) -> {
         ListNBT trades = new ListNBT();
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));

         for (Trade trade : playerMap.get(uuid)) {
            try {
               trades.add(NBTSerializer.serialize(trade));
            } catch (Exception var8) {
               var8.printStackTrace();
            }
         }

         playerTradesList.add(trades);
      });
      compound.func_218657_a("PlayerList", playerList);
      compound.func_218657_a("PlayerTradesList", playerTradesList);
      return compound;
   }

   public static GlobalTraderData get(ServerWorld world) {
      return (GlobalTraderData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(GlobalTraderData::new, "the_vault_GlobalTrader");
   }

   public void reset() {
      playerMap.clear();
      this.func_76185_a();
   }
}
