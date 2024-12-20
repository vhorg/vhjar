package iskallia.vault.network.message;

import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.PlayerBlackMarketData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent.Context;

public class ShardTradeTradeMessage {
   private final int tradeIndex;
   private final UUID uuid;
   private final boolean shift;

   public ShardTradeTradeMessage(int tradeIndex, boolean shift, UUID uuid) {
      this.tradeIndex = tradeIndex;
      this.shift = shift;
      this.uuid = uuid;
   }

   public boolean isRandom() {
      return this.tradeIndex == -1;
   }

   public static void encode(ShardTradeTradeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.tradeIndex);
      buffer.writeBoolean(message.shift);
      buffer.writeUUID(message.uuid);
   }

   public static ShardTradeTradeMessage decode(FriendlyByteBuf buffer) {
      return new ShardTradeTradeMessage(buffer.readInt(), buffer.readBoolean(), buffer.readUUID());
   }

   public static void handle(ShardTradeTradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null && sender.containerMenu instanceof ShardTradeContainer tradeContainer) {
               PlayerBlackMarketData var13 = PlayerBlackMarketData.get(sender.getLevel());
               int shardCount = ItemShardPouch.getShardCount(sender.getInventory());
               ItemStack resultStack = null;
               int shardCost;
               if (message.isRandom()) {
                  int playerLevel = PlayerVaultStatsData.get(sender.getLevel()).getVaultStats(sender).getVaultLevel();
                  Set<SoulShardConfig.Trades> tradesList = ModConfigs.SOUL_SHARD.getTrades();
                  SoulShardConfig.Trades tradesUsed = null;

                  for (SoulShardConfig.Trades trades : tradesList) {
                     if (playerLevel >= trades.getMinLevel() && (tradesUsed == null || tradesUsed.getMinLevel() < trades.getMinLevel())) {
                        tradesUsed = trades;
                     }
                  }

                  if (tradesUsed != null) {
                     if (shardCount < tradesUsed.getShardTradePrice()) {
                        return;
                     }

                     shardCost = tradesUsed.getShardTradePrice();
                     resultStack = tradesUsed.getRandomTrade().getItem().copy();
                  } else {
                     shardCost = 1000;
                  }
               } else {
                  PlayerBlackMarketData.BlackMarket.SelectedTrade trade = var13.getBlackMarket(message.uuid).getTrades().get(message.tradeIndex);
                  if (trade == null || shardCount < trade.getShardCost()) {
                     return;
                  }

                  shardCost = trade.getShardCost();
                  resultStack = trade.getStack().copy();
               }

               if (resultStack != null && !resultStack.isEmpty()) {
                  int level = SidedHelper.getVaultLevel(sender);
                  resultStack = LootInitialization.initializeVaultLoot(resultStack, level);
                  IItemHandler playerInv = (IItemHandler)sender.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP)
                     .orElseThrow(IllegalStateException::new);
                  if (ItemShardPouch.reduceShardAmount(sender.getInventory(), shardCost, true)) {
                     if (!message.shift) {
                        ItemStack held = tradeContainer.getCarried();
                        if (!held.isEmpty()) {
                           return;
                        }
                     }

                     if (ItemShardPouch.reduceShardAmount(sender.getInventory(), shardCost, false)) {
                        if (!message.isRandom()) {
                           var13.getBlackMarket(message.uuid).useTrade(message.tradeIndex);
                        }

                        if (message.shift) {
                           ItemStack remainder = MiscUtils.mergeIntoInventory(playerInv, resultStack, false);
                           if (!remainder.isEmpty()) {
                              sender.drop(remainder.copy(), true);
                           }
                        } else {
                           ItemStack held = tradeContainer.getCarried();
                           if (held.isEmpty()) {
                              tradeContainer.setCarried(resultStack);
                           } else {
                              held.grow(resultStack.getCount());
                           }
                        }

                        tradeContainer.broadcastChanges();
                     }

                     sender.level.playSound(null, sender.blockPosition(), ModSounds.VAULT_CHEST_RARE_OPEN, SoundSource.PLAYERS, 1.0F, 0.5F);
                     var13.getBlackMarket(sender).syncToClient(sender.server);
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
