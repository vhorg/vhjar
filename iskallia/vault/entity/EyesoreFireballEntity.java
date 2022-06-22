package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.Explosion.Mode;
import net.minecraftforge.fml.network.NetworkHooks;

public class EyesoreFireballEntity extends ProjectileItemEntity {
   public int explosionPower = 1;

   public EyesoreFireballEntity(EntityType<? extends ProjectileItemEntity> type, World world) {
      super(type, world);
      this.func_213884_b(new ItemStack(Items.field_151059_bz));
   }

   public EyesoreFireballEntity(World world, LivingEntity thrower) {
      super(ModEntities.EYESORE_FIREBALL, thrower, world);
      this.func_213884_b(new ItemStack(Items.field_151059_bz));
   }

   protected void func_70227_a(RayTraceResult result) {
      super.func_70227_a(result);
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_217385_a(null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.explosionPower, Mode.NONE);
         this.func_70106_y();
      }
   }

   protected void func_213868_a(EntityRayTraceResult result) {
      super.func_213868_a(result);
      if (!this.field_70170_p.field_72995_K) {
         Entity target = result.func_216348_a();
         Entity shooter = this.func_234616_v_();
         DamageSource source = new IndirectEntityDamageSource("fireball", this, shooter).func_82726_p();
         float damage = ModConfigs.EYESORE.basicAttack.getDamage(this);
         target.func_70097_a(source, damage);
         if (shooter instanceof LivingEntity) {
            this.func_174815_a((LivingEntity)shooter, target);
         }
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         this.playEffects();
      }
   }

   public void playEffects() {
      Vector3d vec = this.func_213303_ch();

      for (int i = 0; i < 5; i++) {
         Vector3d v = vec.func_72441_c(
            this.field_70146_Z.nextFloat() * 0.4F * (this.field_70146_Z.nextBoolean() ? 1 : -1),
            this.field_70146_Z.nextFloat() * 0.4F * (this.field_70146_Z.nextBoolean() ? 1 : -1),
            this.field_70146_Z.nextFloat() * 0.4F * (this.field_70146_Z.nextBoolean() ? 1 : -1)
         );
         this.field_70170_p.func_195594_a((IParticleData)ModParticles.RED_FLAME.get(), v.field_72450_a, v.field_72448_b, v.field_72449_c, 0.0, 0.0, 0.0);
         this.field_70170_p.func_195594_a(ParticleTypes.field_197631_x, v.field_72450_a, v.field_72448_b, v.field_72449_c, 0.0, 0.0, 0.0);
      }
   }

   public boolean func_241849_j(Entity entity) {
      return super.func_241849_j(entity) && !(entity instanceof EyesoreEntity) && !(entity instanceof EyestalkEntity);
   }

   protected Item func_213885_i() {
      return Items.field_151059_bz;
   }

   public void func_213281_b(CompoundNBT nbt) {
      super.func_213281_b(nbt);
      nbt.func_74768_a("ExplosionPower", this.explosionPower);
   }

   public void func_70037_a(CompoundNBT nbt) {
      super.func_70037_a(nbt);
      if (nbt.func_150297_b("ExplosionPower", 99)) {
         this.explosionPower = nbt.func_74762_e("ExplosionPower");
      }
   }

   public boolean func_70067_L() {
      return false;
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      return false;
   }

   public IPacket<?> func_213297_N() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
