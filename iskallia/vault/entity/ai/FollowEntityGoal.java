package iskallia.vault.entity.ai;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class FollowEntityGoal<T extends Mob, O extends LivingEntity> extends GoalTask<T> {
   private O owner;
   private final double followSpeed;
   private final PathNavigation navigator;
   private int timeToRecalcPath;
   private final float maxDist;
   private final float minDist;
   private float oldWaterCost;
   private final boolean teleportToLeaves;
   private final Supplier<Optional<O>> ownerSupplier;

   public FollowEntityGoal(T entity, double speed, float minDist, float maxDist, boolean teleportToLeaves, Supplier<Optional<O>> ownerSupplier) {
      super(entity);
      this.followSpeed = speed;
      this.navigator = entity.getNavigation();
      this.minDist = minDist;
      this.maxDist = maxDist;
      this.teleportToLeaves = teleportToLeaves;
      this.ownerSupplier = ownerSupplier;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      if (!(this.getEntity().getNavigation() instanceof GroundPathNavigation) && !(this.getEntity().getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public boolean canUse() {
      O owner = this.ownerSupplier.get().orElse(null);
      if (owner == null) {
         return false;
      } else if (owner.isSpectator()) {
         return false;
      } else if (owner.distanceToSqr(this.getEntity()) < this.minDist * this.minDist) {
         return false;
      } else {
         this.owner = owner;
         return true;
      }
   }

   public boolean canContinueToUse() {
      return this.navigator.isDone() ? false : this.getEntity().distanceToSqr(this.owner) > this.maxDist * this.maxDist;
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.getEntity().getPathfindingMalus(BlockPathTypes.WATER);
      this.getEntity().setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
   }

   public void stop() {
      this.owner = null;
      this.navigator.stop();
      this.getEntity().setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
   }

   public void tick() {
      this.getEntity().getLookControl().setLookAt(this.owner, 10.0F, this.getEntity().getMaxHeadXRot());
      if (--this.timeToRecalcPath <= 0) {
         if (!this.getEntity().isLeashed() && !this.getEntity().isPassenger()) {
            if (this.getEntity().distanceToSqr(this.owner) >= 144.0) {
               this.tryToTeleportNearEntity();
            } else {
               this.navigator.moveTo(this.owner, this.followSpeed);
            }
         }

         this.timeToRecalcPath = 10;
      }
   }

   private void tryToTeleportNearEntity() {
      BlockPos blockpos = this.owner.blockPosition();

      for (int i = 0; i < 10; i++) {
         int j = this.nextInt(-3, 3);
         int k = this.nextInt(-1, 1);
         int l = this.nextInt(-3, 3);
         boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
         if (flag) {
            return;
         }
      }
   }

   private boolean tryToTeleportToLocation(int x, int y, int z) {
      if (Math.abs(x - this.owner.getX()) < 2.0 && Math.abs(z - this.owner.getZ()) < 2.0) {
         return false;
      } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.getEntity().moveTo(x + 0.5, y, z + 0.5, this.getEntity().getYRot(), this.getEntity().getXRot());
         this.navigator.stop();
         return true;
      }
   }

   private boolean isTeleportFriendlyBlock(BlockPos pos) {
      BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.getWorld(), pos.mutable());
      if (pathnodetype != BlockPathTypes.WALKABLE) {
         return false;
      } else {
         BlockState blockstate = this.getWorld().getBlockState(pos.below());
         if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockpos = pos.subtract(this.getEntity().blockPosition());
            return this.getWorld().noCollision(this.getEntity(), this.getEntity().getBoundingBox().move(blockpos));
         }
      }
   }

   private int nextInt(int min, int max) {
      return this.getWorld().getRandom().nextInt(max - min + 1) + min;
   }
}
