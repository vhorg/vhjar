package iskallia.vault.block;

import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.entity.entity.AncientCopperGolemEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEntities;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.pattern.BlockPattern.BlockPatternMatch;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class AncientCopperButtonBlock extends ButtonBlock {
   @Nullable
   private BlockPattern golemBase;
   @Nullable
   private BlockPattern golemFull;

   public AncientCopperButtonBlock(Properties properties) {
      super(false, properties);
   }

   protected SoundEvent getSound(boolean pIsOn) {
      return pIsOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
   }

   public boolean canSpawnGolem(LevelReader pLevel, BlockPos pPos) {
      return this.getOrCreateIronGolemBase().find(pLevel, pPos) != null;
   }

   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      if (!pOldState.is(pState.getBlock()) && !VaultUtils.isVaultLevel(pLevel)) {
         this.trySpawnGolem(pLevel, pPos);
      }
   }

   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      if (!pLevel.isClientSide && !(Boolean)pState.getValue(POWERED)) {
         this.checkPressed(pState, pLevel, pPos);
      }
   }

   private int getPressDuration() {
      return 10;
   }

   private void checkPressed(BlockState pState, Level pLevel, BlockPos pPos) {
      List<? extends Entity> list = pLevel.getEntitiesOfClass(AbstractArrow.class, pState.getShape(pLevel, pPos).bounds().move(pPos));
      boolean flag = !list.isEmpty();
      boolean flag1 = (Boolean)pState.getValue(POWERED);
      if (flag != flag1) {
         pLevel.setBlock(pPos, (BlockState)pState.setValue(POWERED, flag), 3);
         this.updateNeighbours(pState, pLevel, pPos);
         this.playSound((Player)null, pLevel, pPos, flag);
         pLevel.gameEvent(list.stream().findFirst().orElse(null), flag ? GameEvent.BLOCK_PRESS : GameEvent.BLOCK_UNPRESS, pPos);
      }

      if (flag) {
         pLevel.scheduleTick(new BlockPos(pPos), this, this.getPressDuration());
      }
   }

   private void updateNeighbours(BlockState pState, Level pLevel, BlockPos pPos) {
      pLevel.updateNeighborsAt(pPos, this);
      pLevel.updateNeighborsAt(pPos.relative(getConnectedDirection(pState).getOpposite()), this);
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if ((Boolean)pState.getValue(POWERED)) {
         return InteractionResult.CONSUME;
      } else {
         this.press(pState, pLevel, pPos);
         this.playSound(pPlayer, pLevel, pPos, true);
         pLevel.gameEvent(pPlayer, GameEvent.BLOCK_PRESS, pPos);
         return InteractionResult.sidedSuccess(pLevel.isClientSide);
      }
   }

   public void press(BlockState pState, Level pLevel, BlockPos pPos) {
      pLevel.setBlock(pPos, (BlockState)pState.setValue(POWERED, true), 3);
      this.updateNeighbours(pState, pLevel, pPos);
      pLevel.scheduleTick(pPos, this, this.getPressDuration());
   }

   private void trySpawnGolem(Level pLevel, BlockPos pPos) {
      BlockPatternMatch blockpattern$blockpatternmatch = this.getOrCreateIronGolemFull().find(pLevel, pPos);
      if (blockpattern$blockpatternmatch != null) {
         for (int j = 0; j < this.getOrCreateIronGolemFull().getWidth(); j++) {
            for (int k = 0; k < this.getOrCreateIronGolemFull().getHeight(); k++) {
               BlockInWorld blockinworld2 = blockpattern$blockpatternmatch.getBlock(j, k, 0);
               pLevel.setBlock(blockinworld2.getPos(), Blocks.AIR.defaultBlockState(), 2);
               pLevel.levelEvent(2001, blockinworld2.getPos(), Block.getId(blockinworld2.getState()));
            }
         }

         BlockPos blockpos = blockpattern$blockpatternmatch.getBlock(1, 2, 0).getPos();
         AncientCopperGolemEntity golem = (AncientCopperGolemEntity)ModEntities.ANCIENT_COPPER_GOLEM.create(pLevel);
         if (golem != null) {
            golem.moveTo(blockpos.getX() + 0.5, blockpos.getY() + 1.05, blockpos.getZ() + 0.5, 0.0F, 0.0F);
            pLevel.addFreshEntity(golem);

            for (ServerPlayer serverplayer1 : pLevel.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer1, golem);
            }
         }

         for (int i1 = 0; i1 < this.getOrCreateIronGolemFull().getWidth(); i1++) {
            for (int j1 = 0; j1 < this.getOrCreateIronGolemFull().getHeight(); j1++) {
               BlockInWorld blockinworld1 = blockpattern$blockpatternmatch.getBlock(i1, j1, 0);
               pLevel.blockUpdated(blockinworld1.getPos(), Blocks.AIR);
            }
         }
      }
   }

   private BlockPattern getOrCreateIronGolemBase() {
      if (this.golemBase == null) {
         this.golemBase = BlockPatternBuilder.start()
            .aisle(new String[]{"~^~", "~#~"})
            .where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.ANCIENT_COPPER_TRAPDOOR)))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.ANCIENT_COPPER_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR)))
            .build();
      }

      return this.golemBase;
   }

   private BlockPattern getOrCreateIronGolemFull() {
      if (this.golemFull == null) {
         this.golemFull = BlockPatternBuilder.start()
            .aisle(new String[]{"~^~", "B#~"})
            .where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.ANCIENT_COPPER_TRAPDOOR)))
            .where('B', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.ANCIENT_COPPER_BUTTON)))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.ANCIENT_COPPER_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR)))
            .build();
      }

      return this.golemFull;
   }
}
