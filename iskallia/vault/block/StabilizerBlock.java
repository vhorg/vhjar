package iskallia.vault.block;

import com.google.common.collect.Lists;
import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.EffectMessage;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.VoxelUtils;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class StabilizerBlock extends Block implements EntityBlock {
   private static final Random rand = new Random();
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   private static final VoxelShape SHAPE_TOP = makeShape().move(0.0, -1.0, 0.0);
   private static final VoxelShape SHAPE_BOTTOM = makeShape();

   public StabilizerBlock() {
      super(Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 3600000.0F).noOcclusion().noDrops());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   private static VoxelShape makeShape() {
      VoxelShape m1 = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
      VoxelShape m2 = Block.box(2.0, 2.0, 2.0, 14.0, 29.0, 14.0);
      return VoxelUtils.combineAll(BooleanOp.OR, m1, m2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.STABILIZER_TILE_ENTITY, StabilizerTileEntity::tick);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE_BOTTOM;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState downState = world.getBlockState(pos.below());
         return !(downState.getBlock() instanceof StabilizerBlock) ? InteractionResult.SUCCESS : this.use(downState, world, pos.below(), player, hand, hit);
      } else {
         if (!world.isClientSide() && world instanceof ServerLevel && hand == InteractionHand.MAIN_HAND) {
            this.spawnNoVoteParticles(world, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private void spawnNoVoteParticles(Level world, BlockPos pos) {
      for (int i = 0; i < 40; i++) {
         Vec3 particlePos = new Vec3(
            pos.getX() - 0.5 + rand.nextFloat() * 2.0F, pos.getY() + rand.nextFloat() * 8.0F, pos.getZ() - 0.5 + rand.nextFloat() * 2.0F
         );
         EffectMessage pkt = new EffectMessage(EffectMessage.Type.COLORED_FIREWORK, particlePos).addData(buf -> buf.writeInt(10027008));
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), pkt);
      }
   }

   private boolean startPoll(ServerLevel world, BlockPos pos) {
      return false;
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      super.onRemove(state, world, pos, newState, isMoving);
      if (!state.is(newState.getBlock())) {
         if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState otherState = world.getBlockState(pos.below());
            if (otherState.is(state.getBlock())) {
               world.removeBlock(pos.below(), isMoving);
            }
         } else {
            BlockState otherState = world.getBlockState(pos.above());
            if (otherState.is(state.getBlock())) {
               world.removeBlock(pos.above(), isMoving);
            }
         }
      }
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      return Lists.newArrayList();
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.STABILIZER_TILE_ENTITY.create(pPos, pState) : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF});
   }
}
