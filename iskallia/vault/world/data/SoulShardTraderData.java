package iskallia.vault.world.data;

import iskallia.vault.Vault;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SoulShardTraderData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_SoulShardTrader";
   private static final Random rand = new Random();
   private long nextReset = 0L;
   private long seed = 0L;
   private final Map<Integer, SoulShardTraderData.SelectedTrade> trades = new HashMap<>();

   public SoulShardTraderData() {
      this("the_vault_SoulShardTrader");
   }

   public SoulShardTraderData(String name) {
      super(name);
   }

   public void resetDailyTrades() {
      this.resetTrades();
      Vault.LOGGER.info("Reset SoulShard Trades!");
   }

   public void resetTrades() {
      this.trades.clear();

      for (int i = 0; i < 3; i++) {
         this.trades.put(i, new SoulShardTraderData.SelectedTrade(ModConfigs.SOUL_SHARD.getRandomTrade(rand)));
      }

      if (ModConfigs.RAID_EVENT_CONFIG.isEnabled()) {
         ItemStack eventSeal = new ItemStack(ModItems.CRYSTAL_SEAL_RAID);
         ItemVaultCrystalSeal.setEventKey(eventSeal, "raid");
         SoulShardTraderData.SelectedTrade eventTrade = new SoulShardTraderData.SelectedTrade(eventSeal, ModConfigs.RAID_EVENT_CONFIG.getSoulShardTradeCost());
         eventTrade.isInfinite = true;
         this.trades.put(0, eventTrade);
      }

      this.nextReset = System.currentTimeMillis() / 1000L + Duration.ofDays(1L).getSeconds();
      this.func_76185_a();
   }

   public boolean useTrade(int tradeId) {
      SoulShardTraderData.SelectedTrade trade = this.trades.get(tradeId);
      if (trade != null && trade.isInfinite) {
         return true;
      } else {
         this.trades.remove(tradeId);
         this.func_76185_a();
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
      this.func_76185_a();
   }

   public void func_76185_a() {
      super.func_76185_a();
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), this.getUpdatePacket());
   }

   public ShardTradeMessage getUpdatePacket() {
      return new ShardTradeMessage(ModConfigs.SOUL_SHARD.getShardTradePrice(), this.seed, this.getTrades());
   }

   public void syncTo(ServerPlayerEntity player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void openTradeContainer(ServerPlayerEntity player) {
      NetworkHooks.openGui(player, new INamedContainerProvider() {
         public ITextComponent func_145748_c_() {
            return new StringTextComponent("Soul Shard Trading");
         }

         public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerx) {
            return new ShardTradeContainer(windowId, playerInventory, SoulShardTraderData.this.new TraderInventory());
         }
      });
   }

   public void func_76184_a(CompoundNBT tag) {
      this.trades.clear();
      this.seed = tag.func_74763_f("seed");
      ListNBT list = tag.func_150295_c("trades", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundNBT tradeTag = list.func_150305_b(i);
         this.trades.put(tradeTag.func_74762_e("index"), new SoulShardTraderData.SelectedTrade(tradeTag.func_74775_l("trade")));
      }

      this.nextReset = tag.func_74763_f("nextReset");
      if (this.nextReset < System.currentTimeMillis() / 1000L) {
         this.seed = rand.nextLong();
         this.resetTrades();
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT tag) {
      ListNBT list = new ListNBT();
      this.trades.forEach((index, trade) -> {
         CompoundNBT tradeTag = new CompoundNBT();
         tradeTag.func_74768_a("index", index);
         tradeTag.func_218657_a("trade", trade.serialize());
         list.add(tradeTag);
      });
      tag.func_218657_a("trades", list);
      tag.func_74772_a("seed", this.seed);
      tag.func_74772_a("nextReset", this.nextReset);
      return tag;
   }

   public static SoulShardTraderData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static SoulShardTraderData get(MinecraftServer server) {
      return (SoulShardTraderData)server.func_241755_D_().func_217481_x().func_215752_a(SoulShardTraderData::new, "the_vault_SoulShardTrader");
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

      public SelectedTrade(CompoundNBT tag) {
         this.stack = ItemStack.func_199557_a(tag.func_74775_l("stack"));
         this.shardCost = tag.func_74762_e("cost");
         this.isInfinite = tag.func_74767_n("infinite");
      }

      public int getShardCost() {
         return this.shardCost;
      }

      public ItemStack getStack() {
         return this.stack.func_77946_l();
      }

      public boolean isInfinite() {
         return this.isInfinite;
      }

      public CompoundNBT serialize() {
         CompoundNBT tag = new CompoundNBT();
         tag.func_218657_a("stack", this.stack.serializeNBT());
         tag.func_74768_a("cost", this.shardCost);
         tag.func_74757_a("infinite", this.isInfinite);
         return tag;
      }
   }

   public class TraderInventory implements IInventory {
      public int func_70302_i_() {
         return 4;
      }

      public boolean func_191420_l() {
         return false;
      }

      public ItemStack func_70301_a(int index) {
         if (index == 0) {
            return new ItemStack(ModItems.UNKNOWN_ITEM);
         } else {
            SoulShardTraderData.SelectedTrade trade = SoulShardTraderData.this.trades.get(index - 1);
            return trade != null ? trade.getStack() : ItemStack.field_190927_a;
         }
      }

      public ItemStack func_70298_a(int index, int count) {
         if (count <= 0) {
            return ItemStack.field_190927_a;
         } else if (index == 0) {
            return new ItemStack(ModItems.UNKNOWN_ITEM);
         } else {
            if (count > 0) {
               SoulShardTraderData.SelectedTrade trade = SoulShardTraderData.this.trades.get(index - 1);
               if (trade != null) {
                  return trade.getStack();
               }
            }

            return ItemStack.field_190927_a;
         }
      }

      public ItemStack func_70304_b(int index) {
         if (index == 0) {
            return new ItemStack(ModItems.UNKNOWN_ITEM);
         } else {
            SoulShardTraderData.SelectedTrade trade = SoulShardTraderData.this.trades.get(index - 1);
            return trade != null ? trade.getStack() : ItemStack.field_190927_a;
         }
      }

      public void func_70299_a(int index, ItemStack stack) {
      }

      public void func_70296_d() {
         SoulShardTraderData.this.func_76185_a();
      }

      public boolean func_70300_a(PlayerEntity player) {
         return true;
      }

      public void func_174888_l() {
      }
   }
}
