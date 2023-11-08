package iskallia.vault.block;

import iskallia.vault.block.entity.SparkTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.WendarrSparkParticleMessage;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class SparkBlock extends Block implements EntityBlock {
   public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape SHAPE_GONE = Block.box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final BooleanProperty EXPENDED = BooleanProperty.create("expended");

   public SparkBlock() {
      super(
         Properties.of(Material.STONE)
            .sound(ModSounds.VAULT_GET_SOUND_TYPE)
            .strength(350.0F, 5.0F)
            .noDrops()
            .noOcclusion()
            .isViewBlocking((state, blockGetter, pos) -> false)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(EXPENDED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{EXPENDED});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SparkTileEntity(pos, state);
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
      return level.getBlockEntity(pos, ModBlocks.SPARK_TILE_ENTITY)
         .map(sparkTileEntity -> sparkTileEntity.hasntExpiredYet() ? super.getDestroyProgress(state, player, level, pos) : -1.0F)
         .orElse(-1.0F);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.SPARK_TILE_ENTITY, SparkTileEntity::tick);
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (!level.isClientSide) {
         ModNetwork.CHANNEL
            .send(PacketDistributor.ALL.noArg(), new WendarrSparkParticleMessage(new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F)));
         level.playSound(null, pos, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 0.5F, 1.75F);
         level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
      }

      return level.setBlockAndUpdate(pos, ModBlocks.CONVERTED_SPARK.defaultBlockState());
   }

   public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
      return super.getExplosionResistance(state, level, pos, explosion);
   }

   public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
      return level.getBlockEntity(pos, ModBlocks.SPARK_TILE_ENTITY).filter(sparkTileEntity -> sparkTileEntity.getLifeTimePercentage() == 0.0F).isEmpty();
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return pLevel.getBlockEntity(pPos, ModBlocks.SPARK_TILE_ENTITY)
         .map(sparkTileEntity -> sparkTileEntity.getLifeTimePercentage() == 0.0F ? SHAPE_GONE : SHAPE)
         .orElse(SHAPE);
   }
}
