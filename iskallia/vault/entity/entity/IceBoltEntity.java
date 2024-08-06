package iskallia.vault.entity.entity;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IceBoltEntity extends AbstractArrow {
   private static final EntityDataAccessor<Integer> MODEL = SynchedEntityData.defineId(IceBoltEntity.class, EntityDataSerializers.INT);
   private final Consumer<HitResult> onHit;
   private Vec3 velocity;

   public IceBoltEntity(EntityType<? extends AbstractArrow> type, Level world) {
      super(type, world);
      this.onHit = result -> {};
      this.initialize();
   }

   public IceBoltEntity(LivingEntity thrower, IceBoltEntity.Model model, Consumer<HitResult> onHit) {
      super(ModEntities.ICE_BOLT, thrower, thrower.getLevel());
      this.setModel(model);
      this.onHit = onHit;
      this.initialize();
   }

   public void shootFromRotation(Entity projectile, float x, float y, float z, float velocity, float inaccuracy) {
      super.shootFromRotation(projectile, x, y, z, velocity, inaccuracy);
      this.velocity = this.getDeltaMovement();
   }

   private void initialize() {
      this.velocity = Vec3.ZERO;
      this.pickup = Pickup.DISALLOWED;
      this.setNoGravity(true);
      this.setBaseDamage(0.0);
   }

   public IceBoltEntity.Model getModel() {
      int index = (Integer)this.entityData.get(MODEL);
      return index >= 0 && index < IceBoltEntity.Model.values().length ? IceBoltEntity.Model.values()[index] : null;
   }

   public void setModel(IceBoltEntity.Model model) {
      this.entityData.set(MODEL, model.ordinal());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(MODEL, -1);
   }

   public byte getPierceLevel() {
      return 0;
   }

   public void tick() {
      if (this.level.isClientSide() && !this.inGround && this.tickCount > 2) {
         ParticleEngine engine = Minecraft.getInstance().particleEngine;

         for (int i = 0; i < 4; i++) {
            Particle particle = engine.createParticle(
               (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
               this.position().x + this.getDeltaMovement().x / i,
               this.position().y + this.getDeltaMovement().y / i,
               this.position().z + this.getDeltaMovement().z / i,
               0.0,
               0.0,
               0.0
            );
            if (particle != null) {
               if (this.random.nextBoolean()) {
                  particle.setColor(0.6F, 0.7F, 0.9F);
                  particle.scale(1.0F);
               } else {
                  particle.setColor(0.7F, 0.8F, 1.0F);
                  particle.scale(0.45F);
               }
            }
         }
      }

      super.tick();
      this.tickDespawn();
   }

   protected void onHit(@NotNull HitResult result) {
      ActiveFlags.IS_AOE_ATTACKING
         .runIfNotSet(
            () -> {
               super.onHit(result);
               if (this.level.isClientSide()) {
                  ParticleEngine engine = Minecraft.getInstance().particleEngine;

                  for (int i = 0; i < 12; i++) {
                     Particle particle = engine.createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        this.position().x + this.getDeltaMovement().x / i,
                        this.position().y + this.getDeltaMovement().y / i,
                        this.position().z + this.getDeltaMovement().z / i,
                        0.0,
                        0.0,
                        0.0
                     );
                     if (particle != null) {
                        if (this.random.nextBoolean()) {
                           particle.setColor(0.6F, 0.7F, 0.9F);
                           particle.scale(1.5F);
                        } else {
                           particle.setColor(0.7F, 0.8F, 1.0F);
                           particle.scale(0.7F);
                        }
                     }
                  }
               }

               this.onHit.accept(result);
               this.remove(RemovalReason.DISCARDED);
            }
         );
   }

   protected boolean canHitEntity(Entity entity) {
      return entity instanceof LivingEntity && !(entity instanceof Player) && !(entity instanceof EternalEntity) ? super.canHitEntity(entity) : false;
   }

   protected void handleNetherPortal() {
   }

   public boolean canChangeDimensions() {
      return false;
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.GLASS_BREAK;
   }

   protected ItemStack getPickupItem() {
      return ItemStack.EMPTY;
   }

   public void addAdditionalSaveData(CompoundTag nbt) {
      super.addAdditionalSaveData(nbt);
      CompoundTag tag = new CompoundTag();
      tag.putDouble("x", this.velocity.x);
      tag.putDouble("y", this.velocity.y);
      tag.putDouble("z", this.velocity.z);
      nbt.put("velocity", tag);
   }

   public void readAdditionalSaveData(CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      CompoundTag tag = nbt.getCompound("velocity");
      this.velocity = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(double pX, double pY, double pZ) {
      return true;
   }

   public static enum Model {
      ARROW,
      CHUNK;
   }
}
