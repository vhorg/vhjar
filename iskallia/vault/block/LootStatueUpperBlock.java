package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LootStatueUpperBlock extends Block {
   public static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

   public LootStatueUpperBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).strength(1.0F, 3600000.0F));
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(HALF, Half.BOTTOM));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{BlockStateProperties.HALF});
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.BLOCK;
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if (pState.getValue(HALF) == Half.BOTTOM) {
         if (pDirection == Direction.UP) {
            if (pNeighborState != this.defaultBlockState().setValue(HALF, Half.TOP)) {
               return Blocks.AIR.defaultBlockState();
            }
         } else if (pDirection == Direction.DOWN && pNeighborState.getBlock() != ModBlocks.LOOT_STATUE) {
            return Blocks.AIR.defaultBlockState();
         }
      } else if (pDirection == Direction.DOWN && pNeighborState != this.defaultBlockState().setValue(HALF, Half.BOTTOM)) {
         return Blocks.AIR.defaultBlockState();
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      int i = pState.getValue(HALF) == Half.BOTTOM ? 1 : 2;
      BlockPos p = pPos.below(i);
      BlockState statue = pLevel.getBlockState(p);
      return statue.use(pLevel, pPlayer, pHand, pHit.withPosition(p));
   }

   public ItemStack getCloneItemStack(BlockState pState, HitResult target, BlockGetter level, BlockPos pPos, Player player) {
      int i = pState.getValue(HALF) == Half.BOTTOM ? 1 : 2;
      BlockPos p = pPos.below(i);
      BlockState statue = level.getBlockState(p);
      return statue.getCloneItemStack(new BlockHitResult(Vec3.atCenterOf(p), Direction.UP, p, false), level, p, player);
   }
}
