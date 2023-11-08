package iskallia.vault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AncientCopperTrapDoorBlock extends TrapDoorBlock {
   public AncientCopperTrapDoorBlock(Properties properties) {
      super(properties);
   }

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Entity entity = pContext instanceof EntityCollisionContext ? ((EntityCollisionContext)pContext).getEntity() : null;
      return entity == null || !(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrb)
         ? super.getCollisionShape(pState, pLevel, pPos, pContext)
         : Shapes.empty();
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      pState = (BlockState)pState.cycle(OPEN);
      pLevel.setBlock(pPos, pState, 2);
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      this.playSound(pPlayer, pLevel, pPos, (Boolean)pState.getValue(OPEN));
      return InteractionResult.sidedSuccess(pLevel.isClientSide);
   }
}
