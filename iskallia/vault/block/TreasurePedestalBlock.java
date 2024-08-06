package iskallia.vault.block;

import iskallia.vault.block.entity.TreasurePedestalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TreasurePedestalBlock extends Block implements EntityBlock, GameMasterBlock {
   public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

   public TreasurePedestalBlock() {
      super(Properties.of(Material.METAL, MaterialColor.GOLD).noOcclusion().strength(3600000.0F, 3600000.0F));
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level world, BlockState state, BlockEntityType<A> type) {
      return BlockHelper.getTicker(type, ModBlocks.TREASURE_PEDESTAL_TILE_ENTITY, TreasurePedestalTileEntity::tick);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public void attack(BlockState state, Level level, BlockPos pos, Player player) {
      if (!level.isClientSide()) {
         this.breakPedestal(state, level, pos);
      }
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!level.isClientSide()) {
         this.breakPedestal(state, level, pos);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   private void breakPedestal(BlockState state, Level level, BlockPos pos) {
      if (level instanceof ServerLevel sWorld) {
         if (sWorld.getBlockEntity(pos) instanceof TreasurePedestalTileEntity pedestal) {
            if (!pedestal.getContained().isEmpty()) {
               popResource(sWorld, pos, pedestal.getContained());
               pedestal.setContained(ItemStack.EMPTY);
            }

            Vec3 vec3 = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            sWorld.playSound(null, vec3.x, vec3.y, vec3.z, ModSounds.CRATE_OPEN, SoundSource.PLAYERS, 1.0F, 1.0F);
            sWorld.removeBlock(pos, false);
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
            sWorld.sendParticles(particle, vec3.x, vec3.y, vec3.z, 300, 1.0, 1.0, 1.0, 0.5);
         }
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.TREASURE_PEDESTAL_TILE_ENTITY.create(pPos, pState);
   }
}
