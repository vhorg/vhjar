package iskallia.vault.network.message;

import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.SoulShardTraderData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ShardTradeTradeMessage {
   private final int tradeIndex;
   private final boolean shift;

   public ShardTradeTradeMessage(int tradeIndex, boolean shift) {
      this.tradeIndex = tradeIndex;
      this.shift = shift;
   }

   public boolean isRandom() {
      return this.tradeIndex == -1;
   }

   public static void encode(ShardTradeTradeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.tradeIndex);
      buffer.writeBoolean(message.shift);
   }

   public static ShardTradeTradeMessage decode(FriendlyByteBuf buffer) {
      return new ShardTradeTradeMessage(buffer.readInt(), buffer.readBoolean());
   }

   public static void handle(ShardTradeTradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null && sender.containerMenu instanceof ShardTradeContainer tradeContainer) {
            SoulShardTraderData var10 = SoulShardTraderData.get(sender.getLevel());
            int shardCount = ItemShardPouch.getShardCount(sender.getInventory());
            int shardCost;
            ItemStack resultStack;
            if (message.isRandom()) {
               if (shardCount < ModConfigs.SOUL_SHARD.getShardTradePrice()) {
                  return;
               }

               shardCost = ModConfigs.SOUL_SHARD.getShardTradePrice();
               resultStack = ModConfigs.SOUL_SHARD.getRandomTrade().getItem().copy();
            } else {
               SoulShardTraderData.SelectedTrade trade = var10.getTrades().get(message.tradeIndex);
               if (trade == null || shardCount < trade.getShardCost()) {
                  return;
               }

               shardCost = trade.getShardCost();
               resultStack = trade.getStack().copy();
            }

            if (!resultStack.isEmpty()) {
               resultStack = DataTransferItem.doConvertStack(resultStack);
               if (resultStack.getItem() instanceof VaultGearItem gearItem) {
                  gearItem.setPlayerLevel(resultStack, sender);
                  VaultGearHelper.initializeGearRollType(resultStack, sender);
               }

               if (ItemShardPouch.reduceShardAmount(sender.getInventory(), shardCost, true)) {
                  if (message.shift) {
                     if (!MiscUtils.canMergeIntoInventory(sender.getInventory(), resultStack)) {
                        return;
                     }
                  } else {
                     ItemStack held = tradeContainer.getCarried();
                     if (!held.isEmpty()) {
                        return;
                     }
                  }

                  if (ItemShardPouch.reduceShardAmount(sender.getInventory(), shardCost, false)) {
                     if (!message.isRandom()) {
                        var10.useTrade(message.tradeIndex);
                     }

                     if (message.shift) {
                        MiscUtils.mergeIntoInventory(sender.getInventory(), resultStack);
                     } else {
                        ItemStack heldx = tradeContainer.getCarried();
                        if (heldx.isEmpty()) {
                           tradeContainer.setCarried(resultStack);
                        } else {
                           heldx.grow(resultStack.getCount());
                        }
                     }

                     tradeContainer.broadcastChanges();
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
