package iskallia.vault.util;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class BlockBreakHandler {
   private static final List<BlockBreakHandler.IItemDamageHandler> DAMAGE_HANDLER_LIST = new ArrayList<BlockBreakHandler.IItemDamageHandler>() {};

   private static BlockBreakHandler.IItemDamageHandler getDamageHandler(ItemStack itemStack) {
      for (BlockBreakHandler.IItemDamageHandler handler : DAMAGE_HANDLER_LIST) {
         if (handler.matches(itemStack)) {
            return handler;
         }
      }

      return BlockBreakHandler.DefaultItemDamageHandler.INSTANCE;
   }

   protected abstract int getBlockLimit(Player var1);

   protected abstract ItemStack getMiningItemProxy(Player var1);

   protected abstract boolean shouldVoid(ServerLevel var1, ServerPlayer var2, BlockState var3);

   public boolean areaDig(ServerLevel level, ServerPlayer player, BlockPos pos, Block targetBlock) {
      if (targetBlock == Blocks.AIR) {
         return false;
      } else if (!level.getBlockState(pos).canHarvestBlock(level, pos, player)) {
         return false;
      } else {
         ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
         if (heldItem.isDamageableItem()) {
            if (heldItem.getItem() instanceof VaultGearItem gearItem && gearItem.isBroken(heldItem)) {
               return false;
            }

            int usesLeft = heldItem.getMaxDamage() - heldItem.getDamageValue();
            if (usesLeft <= 1) {
               return false;
            }
         }

         BlockBreakHandler.IItemDamageHandler damageHandler = getDamageHandler(heldItem);
         int limit = this.getBlockLimit(player);
         if (limit == 1) {
            this.destroyBlock(level, player, heldItem, damageHandler, pos, this.shouldVoid(level, player, level.getBlockState(pos)), false);
            return true;
         } else if (limit <= 0) {
            return false;
         } else {
            boolean heldItemStartedEmpty = heldItem.isEmpty();
            Set<BlockPos> traversedBlocks = new HashSet<>();
            Queue<BlockPos> positionQueue = new LinkedList<>();
            positionQueue.add(pos);

            while (!positionQueue.isEmpty()) {
               BlockPos headPos = positionQueue.poll();

               for (BlockPos offset : BlockPos.withinManhattanStream(headPos, 1, 1, 1).map(BlockPos::immutable).toList()) {
                  if (traversedBlocks.size() >= limit) {
                     positionQueue.clear();
                     break;
                  }

                  if (!traversedBlocks.contains(offset)) {
                     BlockState blockState = level.getBlockState(offset);
                     if (!blockState.isAir() && blockState.getBlock() == targetBlock) {
                        this.destroyBlock(level, player, heldItem, damageHandler, offset, this.shouldVoid(level, player, blockState), !offset.equals(pos));
                        if (heldItem.isEmpty() && !heldItemStartedEmpty || heldItem.getItem() instanceof VaultGearItem gearItem && gearItem.isBroken(heldItem)) {
                           positionQueue.clear();
                           break;
                        }

                        positionQueue.add(offset);
                        traversedBlocks.add(offset);
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   private void destroyBlock(
      ServerLevel level,
      ServerPlayer player,
      ItemStack mainHandItem,
      BlockBreakHandler.IItemDamageHandler damageHandler,
      BlockPos pos,
      boolean shouldVoid,
      boolean triggerLevelEvent
   ) {
      GameType gameModeForPlayer = player.gameMode.getGameModeForPlayer();
      BlockState blockstate = level.getBlockState(pos);
      int experience = ForgeHooks.onBlockBreakEvent(level, gameModeForPlayer, player, pos);
      if (experience != -1) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         Block block = blockstate.getBlock();
         if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
            level.sendBlockUpdated(pos, blockstate, blockstate, 3);
            level.levelEvent(2001, pos, Block.getId(blockstate));
         } else if (!mainHandItem.onBlockStartBreak(pos, player) && !player.blockActionRestricted(level, pos, gameModeForPlayer)) {
            if (player.gameMode.isCreative()) {
               this.removeBlock(level, player, pos, false);
               return;
            }

            ItemStack mainHandItemProxy = this.getMiningItemProxy(player);
            mainHandItemProxy.mineBlock(level, blockstate, pos, player);
            if (mainHandItemProxy.isEmpty() && !mainHandItem.isEmpty()) {
               ForgeEventFactory.onPlayerDestroyItem(player, mainHandItem, InteractionHand.MAIN_HAND);
               mainHandItem.shrink(1);
            } else if (damageHandler.wasDamaged(mainHandItem, mainHandItemProxy)) {
               damageHandler.applyDamage(mainHandItem, mainHandItemProxy);
            }

            boolean canHarvestBlock = blockstate.canHarvestBlock(level, pos, player) && !shouldVoid;
            if (this.removeBlock(level, player, pos, canHarvestBlock)) {
               if (canHarvestBlock) {
                  block.playerDestroy(level, player, pos, blockstate, blockentity, mainHandItemProxy);
               }

               if (experience > 0) {
                  block.popExperience(level, pos, experience);
               }

               if (triggerLevelEvent) {
                  level.levelEvent(2001, pos, Block.getId(blockstate));
               }
            }
         }
      }
   }

   private boolean removeBlock(ServerLevel level, ServerPlayer player, BlockPos pos, boolean canHarvest) {
      BlockState blockState = level.getBlockState(pos);
      FluidState fluid = this.hasHydroVoid(player) ? Fluids.EMPTY.defaultFluidState() : level.getFluidState(pos);
      boolean removed = blockState.onDestroyedByPlayer(level, pos, player, canHarvest, fluid);
      if (removed) {
         blockState.getBlock().destroy(level, pos, blockState);
      }

      return removed;
   }

   private boolean hasHydroVoid(ServerPlayer player) {
      ItemStack stack = player.getMainHandItem();
      if (stack.getItem() != ModItems.TOOL) {
         return false;
      } else {
         VaultGearData data = VaultGearData.read(stack);
         return data.get(ModGearAttributes.HYDROVOID, VaultGearAttributeTypeMerger.anyTrue());
      }
   }

   private static class DefaultItemDamageHandler implements BlockBreakHandler.IItemDamageHandler {
      public static final BlockBreakHandler.IItemDamageHandler INSTANCE = new BlockBreakHandler.DefaultItemDamageHandler();

      @Override
      public boolean matches(ItemStack itemStack) {
         return true;
      }

      @Override
      public boolean wasDamaged(ItemStack itemStack, ItemStack proxy) {
         return itemStack.getDamageValue() != proxy.getDamageValue();
      }

      @Override
      public void applyDamage(ItemStack itemStack, ItemStack proxy) {
         itemStack.setDamageValue(proxy.getDamageValue());
      }
   }

   private interface IItemDamageHandler {
      boolean matches(ItemStack var1);

      boolean wasDamaged(ItemStack var1, ItemStack var2);

      void applyDamage(ItemStack var1, ItemStack var2);
   }
}
