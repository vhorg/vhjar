package iskallia.vault.item;

import iskallia.vault.entity.entity.TeamTaskScoreboardEntity;
import iskallia.vault.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class TeamTaskScoreboardItem extends BasicItem {
   public TeamTaskScoreboardItem(ResourceLocation id) {
      super(id, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public InteractionResult useOn(UseOnContext pContext) {
      BlockPos blockpos = pContext.getClickedPos();
      Direction direction = pContext.getClickedFace();
      BlockPos blockpos1 = blockpos.relative(direction);
      Player player = pContext.getPlayer();
      ItemStack itemstack = pContext.getItemInHand();
      if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
         return InteractionResult.FAIL;
      } else {
         Level level = pContext.getLevel();
         HangingEntity hangingentity = new TeamTaskScoreboardEntity(level, blockpos1, direction);
         CompoundTag compoundtag = itemstack.getTag();
         if (compoundtag != null) {
            EntityType.updateCustomEntityTag(level, player, hangingentity, compoundtag);
         }

         if (hangingentity.survives()) {
            if (!level.isClientSide) {
               hangingentity.playPlacementSound();
               level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
               level.addFreshEntity(hangingentity);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.CONSUME;
         }
      }
   }

   protected boolean mayPlace(Player pPlayer, Direction pDirection, ItemStack pHangingEntityStack, BlockPos pPos) {
      return !pDirection.getAxis().isVertical() && pPlayer.mayUseItemAt(pPos, pDirection, pHangingEntityStack);
   }
}
