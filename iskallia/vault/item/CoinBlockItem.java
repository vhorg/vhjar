package iskallia.vault.item;

import iskallia.vault.block.CoinPileDecorBlock;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CoinBlockItem extends BlockItem {
   private final List<Component> tooltip = new ArrayList<>();

   public CoinBlockItem(Block block) {
      this(block, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   }

   public CoinBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   public CoinBlockItem withTooltip(Component tooltip) {
      this.tooltip.add(tooltip);
      return this;
   }

   public CoinBlockItem withTooltip(Component... tooltip) {
      this.tooltip.addAll(Arrays.asList(tooltip));
      return this;
   }

   public CoinBlockItem withTooltip(List<Component> tooltip) {
      this.tooltip.addAll(tooltip);
      return this;
   }

   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (!this.tooltip.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.addAll(this.tooltip);
      }
   }

   public InteractionResult place(BlockPlaceContext context) {
      if (!context.canPlace()) {
         return InteractionResult.FAIL;
      } else {
         BlockPlaceContext blockplacecontext = this.updatePlacementContext(context);
         if (blockplacecontext == null) {
            return InteractionResult.FAIL;
         } else {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
            int existingCoins = 0;
            if (blockState.getBlock() instanceof CoinPileDecorBlock) {
               existingCoins = (Integer)blockState.getValue(CoinPileDecorBlock.COINS);
            } else {
               blockState = this.getPlacementState(blockplacecontext);
               if (blockState == null) {
                  return InteractionResult.FAIL;
               }
            }

            ItemStack stack = context.getItemInHand();
            Player player = context.getPlayer();
            boolean isShiftKeyDown = player != null && player.isShiftKeyDown();
            int coinsTillFull = 64 - existingCoins;
            int coinsToAdd = isShiftKeyDown ? Math.min(coinsTillFull, stack.getCount()) : 1;
            int newCoins = existingCoins + coinsToAdd;
            blockState = CoinPileDecorBlock.updateCoinsState(blockState, newCoins);
            if (!this.placeBlock(blockplacecontext, blockState)) {
               return InteractionResult.FAIL;
            } else {
               BlockPos blockpos = blockplacecontext.getClickedPos();
               Level level = blockplacecontext.getLevel();
               ItemStack itemstack = blockplacecontext.getItemInHand();
               BlockState blockstate1 = level.getBlockState(blockpos);
               if (blockstate1.is(blockState.getBlock()) && player instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockpos, itemstack);
               }

               level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
               SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
               level.playSound(
                  player,
                  blockpos,
                  this.getPlaceSound(blockstate1, level, blockpos, context.getPlayer()),
                  SoundSource.BLOCKS,
                  (soundtype.getVolume() + 1.0F) / 2.0F,
                  soundtype.getPitch() * 0.8F
               );
               if (player == null || !player.getAbilities().instabuild) {
                  itemstack.shrink(coinsToAdd);
               }

               return InteractionResult.sidedSuccess(level.isClientSide);
            }
         }
      }
   }
}
