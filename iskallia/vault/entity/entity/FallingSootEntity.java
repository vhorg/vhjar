package iskallia.vault.entity.entity;

import iskallia.vault.block.SootLayerBlock;
import iskallia.vault.init.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FallingSootEntity extends FallingBlockEntity {
   public FallingSootEntity(EntityType<? extends FallingSootEntity> entityType, Level level) {
      super(entityType, level);
   }

   private FallingSootEntity(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
      this(ModEntities.FALLING_SOOT, p_31953_);
      this.blockState = p_31957_;
      this.blocksBuilding = true;
      this.setPos(p_31954_, p_31955_, p_31956_);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = p_31954_;
      this.yo = p_31955_;
      this.zo = p_31956_;
      this.setStartPos(this.blockPosition());
   }

   public static FallingSootEntity fall(Level p_201972_, BlockPos p_201973_, BlockState p_201974_) {
      FallingSootEntity soot = new FallingSootEntity(
         p_201972_,
         p_201973_.getX() + 0.5,
         p_201973_.getY(),
         p_201973_.getZ() + 0.5,
         p_201974_.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)p_201974_.setValue(BlockStateProperties.WATERLOGGED, false) : p_201974_
      );
      p_201972_.setBlock(p_201973_, p_201974_.getFluidState().createLegacyBlock(), 3);
      p_201972_.addFreshEntity(soot);
      return soot;
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemLike pItem) {
      this.dropBlockContent(this.getBlockState(), this.blockPosition());
      return null;
   }

   public void tick() {
      if (this.level.isClientSide) {
         super.tick();
      } else {
         BlockState blockState = this.getBlockState();
         if (blockState.isAir()) {
            this.discard();
         } else {
            Block block = blockState.getBlock();
            if (!this.isNoGravity()) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level.isClientSide) {
               BlockPos pos = this.blockPosition();
               if (this.level.getFluidState(pos).is(FluidTags.WATER)) {
                  this.discard();
                  return;
               }

               if (this.getDeltaMovement().lengthSqr() > 1.0) {
                  BlockHitResult blockhitresult = this.level
                     .clip(
                        new ClipContext(
                           new Vec3(this.xo, this.yo, this.zo), this.position(), net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.SOURCE_ONLY, this
                        )
                     );
                  if (blockhitresult.getType() != Type.MISS && this.level.getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
                     this.discard();
                     return;
                  }
               }

               if (!this.onGround) {
                  if (!this.level.isClientSide
                     && (this.time > 100 && (pos.getY() <= this.level.getMinBuildHeight() || pos.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                     if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(block);
                     }

                     this.discard();
                  }
               } else {
                  BlockState onState = this.level.getBlockState(pos);
                  this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                  if (!onState.is(Blocks.MOVING_PISTON)) {
                     boolean canBeReplaced = onState.canBeReplaced(
                        new DirectionalPlaceContext(this.level, pos, Direction.DOWN, new ItemStack(blockState.getBlock().asItem()), Direction.UP)
                     );
                     boolean spaceBelow = isSpaceBelow(this.level.getBlockState(pos.below()));
                     boolean canSurvive = blockState.canSurvive(this.level, pos) && !spaceBelow;
                     if (canBeReplaced && canSurvive) {
                        int remaining = 0;
                        if (onState.is(blockState.getBlock())) {
                           int layers = (Integer)blockState.getValue(SootLayerBlock.LAYERS);
                           int toLayers = (Integer)onState.getValue(SootLayerBlock.LAYERS);
                           int total = layers + toLayers;
                           int target = Mth.clamp(total, 1, 8);
                           remaining = total - target;
                           blockState = (BlockState)blockState.setValue(SootLayerBlock.LAYERS, target);
                        }

                        if (this.level.setBlock(pos, blockState, 3)) {
                           ((ServerLevel)this.level)
                              .getChunkSource()
                              .chunkMap
                              .broadcast(this, new ClientboundBlockUpdatePacket(pos, this.level.getBlockState(pos)));
                           if (block instanceof Fallable fallable) {
                              fallable.onLand(this.level, pos, blockState, onState, this);
                           }

                           this.discard();
                           if (remaining != 0) {
                              BlockPos above = pos.above();
                              blockState = (BlockState)blockState.setValue(SootLayerBlock.LAYERS, remaining);
                              if (this.level.getBlockState(above).getMaterial().isReplaceable() && !this.level.setBlock(above, blockState, 3)) {
                                 ((ServerLevel)this.level)
                                    .getChunkSource()
                                    .chunkMap
                                    .broadcast(this, new ClientboundBlockUpdatePacket(above, this.level.getBlockState(above)));
                                 this.dropBlockContent(blockState, pos);
                              }
                           }

                           return;
                        }
                     }

                     this.discard();
                     if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.callOnBrokenAfterFall(block, pos);
                        this.dropBlockContent(blockState, pos);
                     }
                  }
               }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
         }
      }
   }

   public static boolean isSpaceBelow(BlockState pState) {
      Material material = pState.getMaterial();
      return pState.isAir() || material.isLiquid() || material.isReplaceable() && !(pState.getBlock() instanceof SootLayerBlock);
   }

   private void dropBlockContent(BlockState state, BlockPos pos) {
      Block.dropResources(state, this.level, pos, null, null, ItemStack.EMPTY);
      this.level.levelEvent(null, 2001, pos, Block.getId(state));
   }
}
