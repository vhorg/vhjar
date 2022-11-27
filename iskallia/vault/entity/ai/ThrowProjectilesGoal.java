package iskallia.vault.entity.ai;

import java.util.Random;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThrowProjectilesGoal<T extends Mob> extends GoalTask<T> {
   private final int chance;
   private final int count;
   private final ThrowProjectilesGoal.Projectile projectile;
   private ItemStack oldStack;
   private int progress;

   public ThrowProjectilesGoal(T entity, int chance, int count, ThrowProjectilesGoal.Projectile projectile) {
      super(entity);
      this.chance = chance;
      this.count = count;
      this.projectile = projectile;
   }

   public boolean canUse() {
      return this.getEntity().getTarget() != null && this.getWorld().random.nextInt(this.chance) == 0;
   }

   public boolean canContinueToUse() {
      return this.getEntity().getTarget() != null && this.progress < this.count;
   }

   public void start() {
      this.oldStack = this.getEntity().getItemBySlot(EquipmentSlot.OFFHAND);
      this.getEntity().setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SNOWBALL));
   }

   public void tick() {
      if (this.getWorld().random.nextInt(3) == 0) {
         Entity throwEntity = this.projectile.create(this.getWorld(), this.getEntity());
         LivingEntity target = this.getEntity().getTarget();
         if (target != null) {
            double d0 = target.getEyeY() - 1.1F;
            double d1 = target.getX() - this.getEntity().getX();
            double d2 = d0 - throwEntity.getY();
            double d3 = target.getZ() - this.getEntity().getZ();
            float f = Mth.sqrt((float)(d1 * d1 + d3 * d3)) * 0.2F;
            this.shoot(throwEntity, d1, d2 + f, d3, 1.6F, 4.0F, this.getWorld().random);
            this.getWorld()
               .playSound(
                  null,
                  this.getEntity().blockPosition(),
                  SoundEvents.SNOW_GOLEM_SHOOT,
                  SoundSource.HOSTILE,
                  1.0F,
                  0.4F / (this.getWorld().random.nextFloat() * 0.4F + 0.8F)
               );
            this.getWorld().addFreshEntity(throwEntity);
         }

         this.progress++;
      }
   }

   public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
      Vec3 vector3d = new Vec3(x, y, z)
         .normalize()
         .add(rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy)
         .scale(velocity);
      projectile.setDeltaMovement(vector3d);
      float f = Mth.sqrt((float)vector3d.horizontalDistanceSqr());
      projectile.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 180.0F / (float)Math.PI));
      projectile.setXRot((float)(Mth.atan2(vector3d.y, f) * 180.0F / (float)Math.PI));
      projectile.yRotO = projectile.getYRot();
      projectile.xRotO = projectile.getXRot();
   }

   public void stop() {
      this.getEntity().setItemSlot(EquipmentSlot.OFFHAND, this.oldStack);
      this.oldStack = ItemStack.EMPTY;
      this.progress = 0;
   }

   public interface Projectile {
      Entity create(Level var1, LivingEntity var2);
   }
}
