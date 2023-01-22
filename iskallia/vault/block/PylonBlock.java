package iskallia.vault.block;

import iskallia.vault.block.entity.PylonTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerPylons;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PylonBlock extends Block implements EntityBlock {
   private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 18.0, 14.0);

   public PylonBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.METAL).strength(-1.0F, 3600000.0F).noDrops());
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   @Nonnull
   public RenderShape getRenderShape(@Nonnull BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return ModBlocks.PYLON_TILE_ENTITY.create(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.PYLON_TILE_ENTITY, PylonTileEntity::tick);
   }

   @Nonnull
   public InteractionResult use(
      @Nonnull BlockState state,
      @Nonnull Level world,
      @Nonnull BlockPos pos,
      @Nonnull Player player,
      @Nonnull InteractionHand hand,
      @Nonnull BlockHitResult hit
   ) {
      if (world.getBlockEntity(pos) instanceof PylonTileEntity pylon && !pylon.isConsumed()) {
         if (!world.isClientSide) {
            PlayerPylons.add(player, pylon.config);
            pylon.setConsumed(true);
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }
}
