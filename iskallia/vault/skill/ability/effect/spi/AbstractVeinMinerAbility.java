package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.VeinMinerConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractHoldAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class AbstractVeinMinerAbility<C extends VeinMinerConfig> extends AbstractHoldAbility<C> {
   private static final List<AbstractVeinMinerAbility.IItemDamageHandler> DAMAGE_HANDLER_LIST = new ArrayList<AbstractVeinMinerAbility.IItemDamageHandler>() {};

   private static AbstractVeinMinerAbility.IItemDamageHandler getDamageHandler(ItemStack itemStack) {
      for (AbstractVeinMinerAbility.IItemDamageHandler handler : DAMAGE_HANDLER_LIST) {
         if (handler.matches(itemStack)) {
            return handler;
         }
      }

      return AbstractVeinMinerAbility.DefaultItemDamageHandler.INSTANCE;
   }

   @Override
   public String getAbilityGroupName() {
      return "Vein Miner";
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onBlockMined(BreakEvent event) {
      if (!event.getWorld().isClientSide()
         && !(event.getPlayer() instanceof FakePlayer)
         && event.getPlayer() instanceof ServerPlayer player
         && event.getWorld() instanceof ServerLevel level) {
         if (!this.isItemDenied(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            AbilityTree abilityTree = PlayerAbilitiesData.get(level).getAbilities(player);
            AbilityNode<?, ?> node = abilityTree.getSelectedAbility();
            if (node != null && node.getAbility() == this && abilityTree.isAbilityActive(node)) {
               ActiveFlags.IS_AOE_MINING.runIfNotSet(() -> {
                  C config = (C)node.getAbilityConfig();
                  BlockPos pos = event.getPos();
                  BlockState blockState = level.getBlockState(pos);
                  if (this.areaDig(config, level, player, pos, blockState.getBlock())) {
                     event.setCanceled(true);
                  }

                  abilityTree.setSwappingLocked(true);
               });
            }
         }
      }
   }

   private boolean isItemDenied(ItemStack itemStack) {
      return ModConfigs.ABILITIES_VEIN_MINER_DENY_CONFIG.isItemDenied(itemStack);
   }

   private boolean areaDig(C config, ServerLevel level, ServerPlayer player, BlockPos pos, Block targetBlock) {
      if (targetBlock == Blocks.AIR) {
         return false;
      } else if (!level.getBlockState(pos).canHarvestBlock(level, pos, player)) {
         return false;
      } else {
         ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
         boolean heldItemStartedEmpty = heldItem.isEmpty();
         if (heldItem.isDamageableItem()) {
            int usesLeft = heldItem.getMaxDamage() - heldItem.getDamageValue();
            if (usesLeft <= 1) {
               return false;
            }
         }

         AbstractVeinMinerAbility.IItemDamageHandler damageHandler = getDamageHandler(heldItem);
         int limit = config.getBlockLimit();
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
                     this.destroyBlock(config, level, player, heldItem, damageHandler, offset, this.shouldVoid(level, player, blockState));
                     if (heldItem.isEmpty() && !heldItemStartedEmpty) {
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

   private void destroyBlock(
      C config,
      ServerLevel level,
      ServerPlayer player,
      ItemStack mainHandItem,
      AbstractVeinMinerAbility.IItemDamageHandler damageHandler,
      BlockPos pos,
      boolean shouldVoid
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

            ItemStack mainHandItemProxy = this.getVeinMiningItemProxy(player, config);
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

               level.levelEvent(2001, pos, Block.getId(blockstate));
            }
         }
      }
   }

   private boolean removeBlock(ServerLevel level, ServerPlayer player, BlockPos pos, boolean canHarvest) {
      BlockState blockState = level.getBlockState(pos);
      boolean removed = blockState.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
      if (removed) {
         blockState.getBlock().destroy(level, pos, blockState);
      }

      return removed;
   }

   protected abstract ItemStack getVeinMiningItemProxy(Player var1, C var2);

   protected abstract boolean shouldVoid(ServerLevel var1, ServerPlayer var2, BlockState var3);

   private static class DefaultItemDamageHandler implements AbstractVeinMinerAbility.IItemDamageHandler {
      public static final AbstractVeinMinerAbility.IItemDamageHandler INSTANCE = new AbstractVeinMinerAbility.DefaultItemDamageHandler();

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
