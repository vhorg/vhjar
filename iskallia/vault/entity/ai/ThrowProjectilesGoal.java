package iskallia.vault.entity.ai;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ThrowProjectilesGoal<T extends MobEntity> extends GoalTask<T> {
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

   public boolean func_75250_a() {
      return this.getEntity().func_70638_az() != null && this.getWorld().field_73012_v.nextInt(this.chance) == 0;
   }

   public boolean func_75253_b() {
      return this.getEntity().func_70638_az() != null && this.progress < this.count;
   }

   public void func_75249_e() {
      this.oldStack = this.getEntity().func_184582_a(EquipmentSlotType.OFFHAND);
      this.getEntity().func_184201_a(EquipmentSlotType.OFFHAND, new ItemStack(Items.field_151126_ay));
   }

   public void func_75246_d() {
      if (this.getWorld().field_73012_v.nextInt(3) == 0) {
         Entity throwEntity = this.projectile.create(this.getWorld(), this.getEntity());
         LivingEntity target = this.getEntity().func_70638_az();
         if (target != null) {
            double d0 = target.func_226280_cw_() - 1.1F;
            double d1 = target.func_226277_ct_() - this.getEntity().func_226277_ct_();
            double d2 = d0 - throwEntity.func_226278_cu_();
            double d3 = target.func_226281_cx_() - this.getEntity().func_226281_cx_();
            float f = MathHelper.func_76133_a(d1 * d1 + d3 * d3) * 0.2F;
            this.shoot(throwEntity, d1, d2 + f, d3, 1.6F, 4.0F, this.getWorld().field_73012_v);
            this.getWorld()
               .func_184133_a(
                  null,
                  this.getEntity().func_233580_cy_(),
                  SoundEvents.field_187805_fE,
                  SoundCategory.HOSTILE,
                  1.0F,
                  0.4F / (this.getWorld().field_73012_v.nextFloat() * 0.4F + 0.8F)
               );
            this.getWorld().func_217376_c(throwEntity);
         }

         this.progress++;
      }
   }

   public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
      Vector3d vector3d = new Vector3d(x, y, z)
         .func_72432_b()
         .func_72441_c(rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy)
         .func_186678_a(velocity);
      projectile.func_213317_d(vector3d);
      float f = MathHelper.func_76133_a(Entity.func_213296_b(vector3d));
      projectile.field_70177_z = (float)(MathHelper.func_181159_b(vector3d.field_72450_a, vector3d.field_72449_c) * 180.0F / (float)Math.PI);
      projectile.field_70125_A = (float)(MathHelper.func_181159_b(vector3d.field_72448_b, f) * 180.0F / (float)Math.PI);
      projectile.field_70126_B = projectile.field_70177_z;
      projectile.field_70127_C = projectile.field_70125_A;
   }

   public void func_75251_c() {
      this.getEntity().func_184201_a(EquipmentSlotType.OFFHAND, this.oldStack);
      this.oldStack = ItemStack.field_190927_a;
      this.progress = 0;
   }

   public interface Projectile {
      Entity create(World var1, LivingEntity var2);
   }
}
