package iskallia.vault.entity.ai;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal.Flag;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;

public class FollowEntityGoal<T extends MobEntity, O extends LivingEntity> extends GoalTask<T> {
   private O owner;
   private final double followSpeed;
   private final PathNavigator navigator;
   private int timeToRecalcPath;
   private final float maxDist;
   private final float minDist;
   private float oldWaterCost;
   private final boolean teleportToLeaves;
   private final Supplier<Optional<O>> ownerSupplier;

   public FollowEntityGoal(T entity, double speed, float minDist, float maxDist, boolean teleportToLeaves, Supplier<Optional<O>> ownerSupplier) {
      super(entity);
      this.followSpeed = speed;
      this.navigator = entity.func_70661_as();
      this.minDist = minDist;
      this.maxDist = maxDist;
      this.teleportToLeaves = teleportToLeaves;
      this.ownerSupplier = ownerSupplier;
      this.func_220684_a(EnumSet.of(Flag.MOVE, Flag.LOOK));
      if (!(this.getEntity().func_70661_as() instanceof GroundPathNavigator) && !(this.getEntity().func_70661_as() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public boolean func_75250_a() {
      O owner = this.ownerSupplier.get().orElse(null);
      if (owner == null) {
         return false;
      } else if (owner.func_175149_v()) {
         return false;
      } else if (owner.func_70068_e(this.getEntity()) < this.minDist * this.minDist) {
         return false;
      } else {
         this.owner = owner;
         return true;
      }
   }

   public boolean func_75253_b() {
      return this.navigator.func_75500_f() ? false : this.getEntity().func_70068_e(this.owner) > this.maxDist * this.maxDist;
   }

   public void func_75249_e() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.getEntity().func_184643_a(PathNodeType.WATER);
      this.getEntity().func_184644_a(PathNodeType.WATER, 0.0F);
   }

   public void func_75251_c() {
      this.owner = null;
      this.navigator.func_75499_g();
      this.getEntity().func_184644_a(PathNodeType.WATER, this.oldWaterCost);
   }

   public void func_75246_d() {
      this.getEntity().func_70671_ap().func_75651_a(this.owner, 10.0F, this.getEntity().func_70646_bf());
      if (--this.timeToRecalcPath <= 0) {
         if (!this.getEntity().func_110167_bD() && !this.getEntity().func_184218_aH()) {
            if (this.getEntity().func_70068_e(this.owner) >= 144.0) {
               this.tryToTeleportNearEntity();
            } else {
               this.navigator.func_75497_a(this.owner, this.followSpeed);
            }
         }

         this.timeToRecalcPath = 10;
      }
   }

   private void tryToTeleportNearEntity() {
      BlockPos blockpos = this.owner.func_233580_cy_();

      for (int i = 0; i < 10; i++) {
         int j = this.nextInt(-3, 3);
         int k = this.nextInt(-1, 1);
         int l = this.nextInt(-3, 3);
         boolean flag = this.tryToTeleportToLocation(blockpos.func_177958_n() + j, blockpos.func_177956_o() + k, blockpos.func_177952_p() + l);
         if (flag) {
            return;
         }
      }
   }

   private boolean tryToTeleportToLocation(int x, int y, int z) {
      if (Math.abs(x - this.owner.func_226277_ct_()) < 2.0 && Math.abs(z - this.owner.func_226281_cx_()) < 2.0) {
         return false;
      } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.getEntity().func_70012_b(x + 0.5, y, z + 0.5, this.getEntity().field_70177_z, this.getEntity().field_70125_A);
         this.navigator.func_75499_g();
         return true;
      }
   }

   private boolean isTeleportFriendlyBlock(BlockPos pos) {
      PathNodeType pathnodetype = WalkNodeProcessor.func_237231_a_(this.getWorld(), pos.func_239590_i_());
      if (pathnodetype != PathNodeType.WALKABLE) {
         return false;
      } else {
         BlockState blockstate = this.getWorld().func_180495_p(pos.func_177977_b());
         if (!this.teleportToLeaves && blockstate.func_177230_c() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockpos = pos.func_177973_b(this.getEntity().func_233580_cy_());
            return this.getWorld().func_226665_a__(this.getEntity(), this.getEntity().func_174813_aQ().func_186670_a(blockpos));
         }
      }
   }

   private int nextInt(int min, int max) {
      return this.getWorld().func_201674_k().nextInt(max - min + 1) + min;
   }
}
