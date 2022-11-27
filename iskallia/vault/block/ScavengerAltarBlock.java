package iskallia.vault.block;

import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ScavengerAltarBlock extends Block implements EntityBlock {
   private static VoxelShape SHAPE = Stream.of(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(4.0, 3.0, 4.0, 12.0, 11.0, 12.0),
         Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0),
         Block.box(1.0, 11.0, 1.0, 15.0, 13.0, 15.0),
         Block.box(2.0, 9.0, 2.0, 14.0, 11.0, 14.0),
         Block.box(2.0, 2.0, 2.0, 14.0, 4.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public ScavengerAltarBlock() {
      super(
         Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
            .requiresCorrectToolForDrops()
            .strength(-1.0F, 3600000.0F)
            .noOcclusion()
            .lightLevel(state -> 12)
      );
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> blockEntityType) {
      return level.isClientSide()
         ? BlockHelper.getTicker(blockEntityType, ModBlocks.SCAVENGER_ALTAR_TILE_ENTITY, ScavengerAltarTileEntity::tickClient)
         : BlockHelper.getTicker(blockEntityType, ModBlocks.SCAVENGER_ALTAR_TILE_ENTITY, ScavengerAltarTileEntity::tickServer);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.SCAVENGER_ALTAR_TILE_ENTITY.create(pPos, pState);
   }

   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (hand != InteractionHand.MAIN_HAND) {
         return InteractionResult.CONSUME;
      } else if (world.getBlockEntity(pos) instanceof ScavengerAltarTileEntity tile) {
         boolean var11 = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
         boolean offHandEmpty = player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
         ItemStack existing = tile.getHeldItem().copy();
         if (offHandEmpty && var11 && existing.isEmpty()) {
            return InteractionResult.PASS;
         } else {
            if (var11 && !offHandEmpty) {
               tile.setHeldItem(player.getItemInHand(InteractionHand.OFF_HAND).copy());
               tile.setItemPlacedBy(player.getUUID());
               player.setItemInHand(InteractionHand.OFF_HAND, existing);
            } else {
               tile.setHeldItem(player.getItemInHand(InteractionHand.MAIN_HAND).copy());
               tile.setItemPlacedBy(player.getUUID());
               player.setItemInHand(InteractionHand.MAIN_HAND, existing);
            }

            tile.ticksToConsume = 40;
            tile.consuming = false;
            world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            tile.setChanged();
            world.sendBlockUpdated(pos, tile.getBlockState(), tile.getBlockState(), 3);
            return InteractionResult.CONSUME;
         }
      } else {
         return InteractionResult.PASS;
      }
   }
}