package iskallia.vault.entity.entity.eyesore;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EyesoreFireballEntity extends ThrowableItemProjectile {
   public int explosionPower = 1;

   public EyesoreFireballEntity(EntityType<? extends ThrowableItemProjectile> type, Level world) {
      super(type, world);
      this.setItem(new ItemStack(Items.FIRE_CHARGE));
   }

   public EyesoreFireballEntity(Level world, LivingEntity thrower) {
      super(ModEntities.EYESORE_FIREBALL, thrower, world);
      this.setItem(new ItemStack(Items.FIRE_CHARGE));
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level.isClientSide) {
         this.level.explode(null, this.getX(), this.getY(), this.getZ(), this.explosionPower, BlockInteraction.NONE);
         this.remove(RemovalReason.DISCARDED);
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      if (!this.level.isClientSide) {
         Entity target = result.getEntity();
         Entity shooter = this.getOwner();
         DamageSource source = new IndirectEntityDamageSource("fireball", this, shooter).setMagic();
         float damage = ModConfigs.EYESORE.basicAttack.getDamage(this);
         target.hurt(source, damage);
         if (shooter instanceof LivingEntity) {
            this.doEnchantDamageEffects((LivingEntity)shooter, target);
         }
      }
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         this.playEffects();
      }
   }

   private void playEffects() {
      Vec3 vec = this.position();

      for (int i = 0; i < 5; i++) {
         Vec3 v = vec.add(
            this.random.nextFloat() * 0.4F * (this.random.nextBoolean() ? 1 : -1),
            this.random.nextFloat() * 0.4F * (this.random.nextBoolean() ? 1 : -1),
            this.random.nextFloat() * 0.4F * (this.random.nextBoolean() ? 1 : -1)
         );
         this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, v.x, v.y, v.z, 0.0, 0.0, 0.0);
         this.level.addParticle(ParticleTypes.FLAME, v.x, v.y, v.z, 0.0, 0.0, 0.0);
      }
   }

   protected Item getDefaultItem() {
      return Items.FIRE_CHARGE;
   }

   public void addAdditionalSaveData(CompoundTag nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putInt("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      if (nbt.contains("ExplosionPower", 99)) {
         this.explosionPower = nbt.getInt("ExplosionPower");
      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      return false;
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
