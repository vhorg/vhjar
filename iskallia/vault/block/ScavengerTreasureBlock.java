package iskallia.vault.block;

import iskallia.vault.block.entity.ScavengerTreasureTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ScavengerTreasureBlock extends BaseEntityBlock {
   private static final VoxelShape BOX = Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);

   public ScavengerTreasureBlock() {
      super(Properties.of(Material.METAL, MaterialColor.GOLD).strength(10.0F, 1.0F).sound(ModSounds.VAULT_GET_SOUND_TYPE));
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return BOX;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.SCAVENGER_TREASURE_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.SCAVENGER_TREASURE_TILE_ENTITY, ScavengerTreasureTileEntity::tick);
   }
}
