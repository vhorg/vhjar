package iskallia.vault.entity.entity;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.StonefallFrostParticleMessage;
import iskallia.vault.util.effect.ScheduledEffectHelper;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class VaultBlizzardShard extends AbstractArrow {
   private boolean grounded;
   private boolean hitTarget;
   private int life;
   public Vec3 prevDeltaMovement = new Vec3(0.0, 0.0, 0.0);
   private boolean leftOwner;
   private boolean hasBeenShot;
   private int slowDuration;
   private int frostbiteDuration;
   private int amplifier;
   private int intervalHypothermiaTicks;
   private float frostbiteChance;
   private LivingEntity entityLocked = null;
   private static final String TAG_INTERVAL_TICKS = "intervalTicks";
   private static final String TAG_REMAINING_INTERVAL_TICKS = "remainingIntervalTicks";
   private static final String TAG_PLAYER_UUID = "playerUUID";
   private static final String TAG_ABILITY_DATA = "the_vault:ability/Nova_Slow";

   public VaultBlizzardShard(EntityType<? extends AbstractArrow> entityType, Level level) {
      super(entityType, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public byte getPierceLevel() {
      return 0;
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision(new AABB(this.position(), this.position()).inflate(0.06));
   }

   public void setEntityLocked(LivingEntity entityLocked) {
      this.entityLocked = entityLocked;
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 vec3 = this.getDeltaMovement();
      this.life = 0;
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getOwner();
      if (entity != null) {
         for (Entity entity1 : this.level
            .getEntities(
               this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), p_37272_ -> !p_37272_.isSpectator() && p_37272_.isPickable()
            )) {
            if (entity1.getRootVehicle() == entity.getRootVehicle()) {
               return false;
            }
         }
      }

      return true;
   }

   public void setSlowDuration(int slowDuration) {
      this.slowDuration = slowDuration;
   }

   public void setFrostbiteDuration(int frostbiteDuration) {
      this.frostbiteDuration = frostbiteDuration;
   }

   public void setAmplifier(int amplifier) {
      this.amplifier = amplifier;
   }

   public void setFrostbiteChance(float frostbiteChance) {
      this.frostbiteChance = frostbiteChance;
   }

   public void setIntervalHypothermiaTicks(int intervalHypothermiaTicks) {
      this.intervalHypothermiaTicks = intervalHypothermiaTicks;
   }

   public boolean isNoGravity() {
      return super.isNoGravity();
   }

   private int getSlowDuration() {
      return this.slowDuration;
   }

   public int getFrostbiteDuration() {
      return this.frostbiteDuration;
   }

   private int getAmplifier() {
      return this.amplifier;
   }

   private int getIntervalHypothermiaTicks() {
      return this.intervalHypothermiaTicks;
   }

   private float getFrostbiteChance() {
      return this.frostbiteChance;
   }

   private void destroy() {
      if (this.entityLocked != null && this.getOwner() instanceof Player player) {
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new StonefallFrostParticleMessage(this.entityLocked.position(), 2.0F));
         this.entityLocked.addEffect(new MobEffectInstance(ModEffects.CHILLED, this.getSlowDuration(), this.getAmplifier()));
         CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, this.entityLocked));
         if (this.random.nextFloat() <= this.getFrostbiteChance()) {
            if (this.entityLocked.hasEffect(ModEffects.GLACIAL_SHATTER)) {
               this.entityLocked.removeEffect(ModEffects.GLACIAL_SHATTER);
            }

            this.entityLocked.addEffect(new MobEffectInstance(ModEffects.GLACIAL_SHATTER, this.getFrostbiteDuration(), this.getAmplifier()));
            this.getLevel().playSound(null, this.position().x, this.position().y, this.position().z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.25F, 0.65F);
         } else {
            this.getLevel().playSound(null, this.position().x, this.position().y, this.position().z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.15F, 0.75F);
         }

         if (!this.entityLocked.hasEffect(ModEffects.HYPOTHERMIA)) {
            ScheduledEffectHelper.invalidateAll(this.entityLocked, ModEffects.HYPOTHERMIA);
            ScheduledEffectHelper.scheduleEffect(this.entityLocked, ModEffects.HYPOTHERMIA.instance(0, true), this.getSlowDuration());
            setAbilityData(this.entityLocked, this.getIntervalHypothermiaTicks(), player.getUUID());
         }
      }

      this.remove(RemovalReason.DISCARDED);
   }

   private static void setAbilityData(LivingEntity livingEntity, int intervalTicks, UUID playerUUID) {
      CompoundTag abilityData = getAbilityData(livingEntity);
      abilityData.putInt("intervalTicks", intervalTicks);
      abilityData.putInt("remainingIntervalTicks", 0);
      abilityData.putUUID("playerUUID", playerUUID);
   }

   private static CompoundTag getAbilityData(LivingEntity livingEntity) {
      CompoundTag persistentData = livingEntity.getPersistentData();
      CompoundTag abilityData = persistentData.getCompound("the_vault:ability/Nova_Slow");
      persistentData.put("the_vault:ability/Nova_Slow", abilityData);
      return abilityData;
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.grounded = true;
      }

      if (this.level.isClientSide() && !this.grounded && this.tickCount > 2) {
         for (int i = 0; i < 2; i++) {
            ParticleEngine pm = Minecraft.getInstance().particleEngine;
            Particle particle = pm.createParticle(
               ParticleTypes.SNOWFLAKE,
               this.position().x + this.getDeltaMovement().x / i,
               this.position().y + this.getDeltaMovement().y / i,
               this.position().z + this.getDeltaMovement().z / i,
               0.0,
               0.0,
               0.0
            );
            if (particle != null) {
               particle.setColor(0.9F, 0.9F, 0.9F);
               particle.scale(0.75F);
            }

            particle = pm.createParticle(
               ParticleTypes.SNOWFLAKE,
               this.position().x + this.getDeltaMovement().x / i,
               this.position().y + this.getDeltaMovement().y / i,
               this.position().z + this.getDeltaMovement().z / i,
               0.0,
               0.0,
               0.0
            );
            if (particle != null) {
               particle.setColor(0.6F, 0.7F, 0.9F);
               particle.scale(0.35F);
            }
         }
      }

      if (!this.level.isClientSide()) {
         if (this.entityLocked != null && !this.entityLocked.isDeadOrDying()) {
            if (!this.grounded) {
               this.setDeltaMovement(
                  this.getDeltaMovement()
                     .add((this.entityLocked.position().x - this.position().x) * 0.02F, 0.0, (this.entityLocked.position().z - this.position().z) * 0.02F)
               );
            }

            if (this.hitTarget) {
               float f = (float)(this.position().y - this.entityLocked.position().y);
               if (f < 0.2F) {
                  this.destroy();
               }
            }

            if (this.position().y < this.entityLocked.position().y) {
               this.destroy();
            }
         } else if (this.tickCount > 20) {
            this.destroy();
         }
      }

      this.prevDeltaMovement = this.getDeltaMovement();
      this.baseTick();
      if (!this.hasBeenShot) {
         this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
         this.hasBeenShot = true;
      }

      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      boolean flag = this.isNoPhysics();
      Vec3 vec3 = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double d0 = vec3.horizontalDistance();
         this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
         this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }

      BlockPos blockpos = this.blockPosition();
      BlockState blockstate = this.level.getBlockState(blockpos);
      if (!blockstate.isAir() && !flag && (this.hitTarget || this.entityLocked == null)) {
         VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
         if (!voxelshape.isEmpty()) {
            Vec3 vec31 = this.position();

            for (AABB aabb : voxelshape.toAabbs()) {
               if (aabb.move(blockpos).contains(vec31)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         this.shakeTime--;
      }

      if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
         this.clearFire();
      }

      if (this.inGround && !flag) {
         if (this.shouldFall()) {
            this.startFalling();
         } else if (!this.level.isClientSide) {
            this.tickDespawn();
         }

         this.inGroundTime++;
      } else {
         this.inGroundTime = 0;
         Vec3 vec32 = this.position();
         Vec3 vec33 = vec32.add(vec3);
         HitResult hitresult = this.level.clip(new ClipContext(vec32, vec33, Block.COLLIDER, Fluid.NONE, this));
         if (hitresult.getType() != Type.MISS) {
            vec33 = hitresult.getLocation();
         }

         while (!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
               hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == Type.ENTITY) {
               Entity entity = ((EntityHitResult)hitresult).getEntity();
               Entity entity1 = this.getOwner();
               if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                  hitresult = null;
                  entityhitresult = null;
               }
            }

            if (hitresult != null && hitresult.getType() != Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
               this.onHit(hitresult);
               this.hasImpulse = true;
            }

            if (entityhitresult == null || this.getPierceLevel() <= 0) {
               break;
            }

            hitresult = null;
         }

         vec3 = this.getDeltaMovement();
         double d5 = vec3.x;
         double d6 = vec3.y;
         double d1 = vec3.z;
         double d7 = this.getX() + d5;
         double d2 = this.getY() + d6;
         double d3 = this.getZ() + d1;
         double d4 = vec3.horizontalDistance();
         if (flag) {
            this.setYRot((float)(Mth.atan2(-d5, -d1) * 180.0F / (float)Math.PI));
         } else {
            this.setYRot((float)(Mth.atan2(d5, d1) * 180.0F / (float)Math.PI));
         }

         this.setXRot((float)(Mth.atan2(d6, d4) * 180.0F / (float)Math.PI));
         this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
         this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
         float f = 0.99F;
         float f1 = 0.05F;
         if (this.isInWater()) {
            for (int j = 0; j < 4; j++) {
               float f2 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25, d2 - d6 * 0.25, d3 - d1 * 0.25, d5, d6, d1);
            }

            f = this.getWaterInertia();
         }

         this.setDeltaMovement(vec3.scale(f));
         if (!this.isNoGravity() && !flag) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - 0.075F, vec34.z);
         }

         this.setPos(d7, d2, d3);
      }
   }

   @NotNull
   protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos, float radius) {
      AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
      return world.getEntitiesOfClass(LivingEntity.class, aabb);
   }

   protected void onHit(@NotNull HitResult result) {
      super.onHit(result);
   }

   protected boolean canHitEntity(Entity entity) {
      return this.entityLocked != null && entity.is(this.entityLocked);
   }

   public void onInsideBubbleColumn(boolean pDownwards) {
   }

   protected void handleNetherPortal() {
   }

   public boolean canChangeDimensions() {
      return false;
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
      return this.grounded ? null : super.findHitEntity(pStartVec, pEndVec);
   }

   public Packet<?> getAddEntityPacket() {
      return super.getAddEntityPacket();
   }

   protected void onHitEntity(EntityHitResult pResult) {
      if (pResult.getEntity() == this.entityLocked && !this.grounded && !this.hitTarget) {
         this.hitTarget = true;
      }
   }

   protected void doPostHurtEffects(LivingEntity pLiving) {
      super.doPostHurtEffects(pLiving);
   }

   protected void onHitBlock(BlockHitResult p_36755_) {
   }

   protected boolean tryPickup(Player p_150196_) {
      return false;
   }

   protected ItemStack getPickupItem() {
      return null;
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   public void playSound(SoundEvent pSound, float pVolume, float pPitch) {
      if (pSound != this.getDefaultHitGroundSoundEvent()) {
         super.playSound(pSound, pVolume, pPitch);
      }
   }

   public void playerTouch(Player pEntity) {
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      Entity entity = this.level.getEntity(pPacket.getData());
      if (entity != null) {
         this.setOwner(entity);
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.grounded = pCompound.getBoolean("Grounded");
      if (pCompound.contains("slowDuration")) {
         this.slowDuration = pCompound.getInt("slowDuration");
      }

      if (pCompound.contains("frostbiteDuration")) {
         this.frostbiteDuration = pCompound.getInt("frostbiteDuration");
      }

      if (pCompound.contains("amplifier")) {
         this.amplifier = pCompound.getInt("amplifier");
      }

      if (pCompound.contains("intervalHypothermiaTicks")) {
         this.intervalHypothermiaTicks = pCompound.getInt("intervalHypothermiaTicks");
      }

      if (pCompound.contains("frostbiteChance")) {
         this.frostbiteChance = pCompound.getFloat("frostbiteChance");
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("Grounded", this.grounded);
      pCompound.putInt("slowDuration", this.slowDuration);
      pCompound.putInt("frostbiteDuration", this.frostbiteDuration);
      pCompound.putInt("amplifier", this.amplifier);
      pCompound.putInt("intervalHypothermiaTicks", this.intervalHypothermiaTicks);
      pCompound.putFloat("frostbiteChance", this.frostbiteChance);
   }

   protected void tickDespawn() {
      this.life++;
      if (this.life >= 20) {
         this.discard();
      }
   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(double pX, double pY, double pZ) {
      return true;
   }
}
