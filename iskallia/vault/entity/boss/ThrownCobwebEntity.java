package iskallia.vault.entity.boss;

import iskallia.vault.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ThrownCobwebEntity extends ThrowableItemProjectile {
   public ThrownCobwebEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public ThrownCobwebEntity(Level level, LivingEntity owner) {
      super(ModEntities.THROWN_COBWEB, owner, level);
   }

   protected Item getDefaultItem() {
      return Items.COBWEB;
   }

   protected void onHitEntity(EntityHitResult hitResult) {
      if (!this.level.isClientSide()) {
         BlockPos entityPos = hitResult.getEntity().getOnPos();
         this.placeCobweb(new BlockPos(entityPos.getX(), this.blockPosition().getY(), entityPos.getZ()));
      }
   }

   private void placeCobweb(BlockPos pos) {
      if (!this.level.getBlockState(pos).isAir()) {
         Vec3 deltaMovement = this.getDeltaMovement();
         deltaMovement = deltaMovement.normalize();
         pos = new BlockPos(pos.getX() - deltaMovement.x(), pos.getY() - deltaMovement.y(), pos.getZ() - deltaMovement.z());
         if (!this.level.getBlockState(pos).isAir()) {
            return;
         }
      }

      this.level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
   }

   protected void onHitBlock(BlockHitResult hitResult) {
      if (!this.level.isClientSide() && this.level.getBlockState(hitResult.getBlockPos()).getBlock() != Blocks.COBWEB) {
         this.placeCobweb(this.blockPosition());
      }
   }

   protected float getGravity() {
      return 0.01F;
   }

   protected void onHit(HitResult pResult) {
      super.onHit(pResult);
      if (!this.level.isClientSide) {
         Type hitType = pResult.getType();
         if (hitType == Type.BLOCK && this.level.getBlockState(((BlockHitResult)pResult).getBlockPos()).getBlock() == Blocks.COBWEB) {
            return;
         }

         this.level.broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }
}
