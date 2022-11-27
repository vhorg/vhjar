package iskallia.vault.block;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.OtherSidePortalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.BurntCrystalItem;
import iskallia.vault.item.OtherSideData;
import iskallia.vault.world.vault.VaultUtils;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class OtherSidePortalBlock extends NetherPortalBlock implements EntityBlock {
   public static final StatePredicate FRAME = (state, reader, p) -> Arrays.stream(ModConfigs.OTHER_SIDE.getValidFrameBlocks())
      .anyMatch(b -> b == state.getBlock());

   public OtherSidePortalBlock() {
      super(Properties.copy(Blocks.NETHER_PORTAL));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Axis.X));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.OTHER_SIDE_PORTAL_TILE_ENTITY.create(pPos, pState);
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
   }

   public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
   }

   public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
      if (!world.isClientSide && entity instanceof Player) {
         if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
            VoxelShape playerVoxel = Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()));
            if (Shapes.joinIsNotEmpty(playerVoxel, state.getShape(world, pos), BooleanOp.AND)) {
               VaultPortalSize current = new VaultPortalSize(world, pos, (Axis)state.getValue(AXIS), FRAME);
               if (current.validatePortal()) {
                  ResourceKey<Level> destinationKey = world.dimension() == VaultMod.OTHER_SIDE_KEY ? Level.OVERWORLD : VaultMod.OTHER_SIDE_KEY;
                  ServerLevel destination = ((ServerLevel)world).getServer().getLevel(destinationKey);
                  if (destination != null) {
                     ServerPlayer player = (ServerPlayer)entity;
                     if (player.isOnPortalCooldown()) {
                        player.setPortalCooldown();
                     } else {
                        BlockEntity te = world.getBlockEntity(pos);
                        OtherSidePortalTileEntity portal = te instanceof OtherSidePortalTileEntity ? (OtherSidePortalTileEntity)te : null;
                        if (portal != null) {
                           OtherSideData data = portal.getData();
                           if (data != null) {
                              BlockPos targetPos = data.getLinkedPos();
                              ResourceKey<Level> targetDim = data.getLinkedDim();
                              if (targetPos != null && targetDim != null) {
                                 ServerLevel target = world.getServer().getLevel(targetDim);
                                 if (target != null) {
                                    if (target.getBlockState(targetPos).getBlock() != ModBlocks.OTHER_SIDE_PORTAL) {
                                       targetPos = BurntCrystalItem.forcePlace((ServerLevel)world, current.getBottomLeft(), target, current);
                                       data.setLinkedPos(targetPos);
                                    }

                                    VaultUtils.moveTo(target, player, new Vec3(targetPos.getX() + 0.2, targetPos.getY(), targetPos.getZ() + 0.2), null);
                                    player.setPortalCooldown();
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor iworld, BlockPos currentPos, BlockPos facingPos) {
      if (!(iworld instanceof ServerLevel)) {
         return state;
      } else {
         Axis facingAxis = facing.getAxis();
         Axis portalAxis = (Axis)state.getValue(AXIS);
         boolean flag = portalAxis != facingAxis && facingAxis.isHorizontal();
         return !flag && !facingState.is(this) && !new VaultPortalSize(iworld, currentPos, portalAxis, FRAME).validatePortal()
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, facing, facingState, iworld, currentPos, facingPos);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
      for (int i = 0; i < 4; i++) {
         double d0 = pos.getX() + rand.nextDouble();
         double d1 = pos.getY() + rand.nextDouble();
         double d2 = pos.getZ() + rand.nextDouble();
         double d3 = (rand.nextFloat() - 0.5) * 0.5;
         double d4 = (rand.nextFloat() - 0.5) * 0.5;
         double d5 = (rand.nextFloat() - 0.5) * 0.5;
         int j = rand.nextInt(2) * 2 - 1;
         if (!world.getBlockState(pos.west()).is(this) && !world.getBlockState(pos.east()).is(this)) {
            d0 = pos.getX() + 0.5 + 0.25 * j;
            d3 = rand.nextFloat() * 2.0F * j;
         } else {
            d2 = pos.getZ() + 0.5 + 0.25 * j;
            d5 = rand.nextFloat() * 2.0F * j;
         }

         world.addParticle(ParticleTypes.WHITE_ASH, d0, d1, d2, d3, d4, d5);
      }
   }
}
