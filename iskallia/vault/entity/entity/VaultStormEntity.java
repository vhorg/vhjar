package iskallia.vault.entity.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.calc.AbilityPowerHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultStormEntity extends Entity {
   private int duration;
   private float radius;
   private float percentAbilityPowerDealt;
   private int intervalTicks;
   private int intervalTicksMax;
   private int frostbiteDuration;
   private int slowDuration;
   private int amplifier;
   private int intervalHypothermiaTicks;
   private float frostbiteChance;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private Entity cachedOwner;
   private static final EntityDataAccessor<Integer> ID_TYPE = SynchedEntityData.defineId(VaultStormEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(VaultStormEntity.class, EntityDataSerializers.FLOAT);
   public static final Predicate<Entity> ENTITY_PREDICATE = entity -> !(entity instanceof Player)
      && entity instanceof LivingEntity livingEntity
      && livingEntity.isAlive();

   public VaultStormEntity(EntityType<? extends VaultStormEntity> type, Level world) {
      super(type, world);
      this.setInvulnerable(true);
      this.radius = 5.0F;
      this.intervalTicks = 0;
      this.intervalTicksMax = 10;
   }

   public VaultStormEntity(
      Level world, double x, double y, double z, float radius, int duration, Entity owner, float percentAbilityPowerDealt, int intervalTicks
   ) {
      this(ModEntities.STORM, world);
      this.setPos(x, y, z);
      this.setYRot((float)(this.random.nextDouble() * 360.0));
      this.intervalTicks = 0;
      this.intervalTicksMax = intervalTicks;
      this.duration = duration;
      this.radius = radius;
      this.entityData.set(RADIUS, radius);
      this.percentAbilityPowerDealt = percentAbilityPowerDealt;
      this.setOwner(owner);
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
         this.cachedOwner = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   public void setOwner(@Nullable Entity pEntity) {
      if (pEntity != null) {
         this.ownerUUID = pEntity.getUUID();
         this.cachedOwner = pEntity;
      }
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

   public int getSlowDuration() {
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

   public void setIdType(int type) {
      this.entityData.set(ID_TYPE, type);
   }

   public int getDuration() {
      return this.duration;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   public boolean isAttackable() {
      return false;
   }

   public SoundSource getSoundSource() {
      return SoundSource.AMBIENT;
   }

   public static Vec3 generatePointInCircle(Vec3 center, double radius, double time, Random random, boolean below) {
      double angle = (Math.PI * 2) * random.nextDouble();
      double distance = Math.sqrt(random.nextDouble());
      double cloudRadius = radius
         + Math.sin(3.0 * angle + time) * radius * 0.13
         + Math.sin(4.0 * (angle + Math.sin(time)) + time * 2.0) * radius * 0.13
         + Math.sin(5.0 * (angle + Math.sin(time)) + time * 3.0) * radius * 0.13;
      double height = (1.0 - distance * distance * distance) / radius * cloudRadius * 0.75;
      if (!below) {
         if (random.nextBoolean()) {
            height *= -1.0;
         }
      } else {
         height *= -1.0;
      }

      double x = center.x() + Math.cos(angle) * cloudRadius * distance;
      double y = center.y() + height;
      double z = center.z() + Math.sin(angle) * cloudRadius * distance;
      return new Vec3(x, y, z);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide()) {
         for (int i = 0; i < 6.0F * this.entityData.get(RADIUS); i++) {
            Vec3 vec3 = generatePointInCircle(this.position(), (Float)this.entityData.get(RADIUS) + 0.5F, this.tickCount / 50.0F, this.random, false);
            ParticleEngine pm = Minecraft.getInstance().particleEngine;
            Particle particle = pm.createParticle(
               (ParticleOptions)ModParticles.STORM_CLOUD.get(), vec3.x(), vec3.y() + this.random.nextDouble() * 0.4F, vec3.z(), 0.0, 0.0, 0.0
            );
            if (VaultStormArrow.StormType.byId((Integer)this.entityData.get(ID_TYPE)) == VaultStormArrow.StormType.BLIZZARD && particle != null) {
               float f = this.random.nextFloat() * 0.1F;
               particle.setColor(0.6F + f, 0.7F + f, 0.9F + f);
            }
         }

         for (int ix = 0; ix < this.entityData.get(RADIUS) / 4.0F; ix++) {
            Vec3 vec3 = generatePointInCircle(this.position(), (Float)this.entityData.get(RADIUS) + 0.5F, this.tickCount / 50.0F, this.random, true);
            if (VaultStormArrow.StormType.byId((Integer)this.entityData.get(ID_TYPE)) == VaultStormArrow.StormType.BASE) {
               this.level.addParticle(ParticleTypes.FALLING_WATER, vec3.x(), vec3.y() + this.random.nextDouble() * 0.4F, vec3.z(), 0.0, 0.0, 0.0);
            } else if (VaultStormArrow.StormType.byId((Integer)this.entityData.get(ID_TYPE)) == VaultStormArrow.StormType.BLIZZARD) {
               this.level.addParticle(ParticleTypes.SNOWFLAKE, vec3.x(), vec3.y() + this.random.nextDouble() * 0.4F, vec3.z(), 0.0, 0.0, 0.0);
            }
         }
      } else {
         if (this.intervalTicks > 0) {
            this.intervalTicks--;
         } else if (this.getOwner() instanceof Player player) {
            if (VaultStormArrow.StormType.byId((Integer)this.entityData.get(ID_TYPE)) == VaultStormArrow.StormType.BASE) {
               DamageSource srcPlayerAttack = DamageSource.playerAttack(player);
               ArrayList<LivingEntity> result = new ArrayList<>();
               getEntitiesInRange(player.level, this.position(), this.radius, ENTITY_PREDICATE, result, 14.0F);
               result.removeIf(entity -> entity instanceof EternalEntity);
               result.removeIf(entity -> entity.isInvulnerableTo(srcPlayerAttack));
               if (!result.isEmpty()) {
                  LivingEntity livingEntity = result.get(player.level.random.nextInt(result.size()));
                  VaultStormEntity.SmiteBolt smiteBolt = (VaultStormEntity.SmiteBolt)ModEntities.THUNDERSTORM_BOLT.create(player.level);
                  if (smiteBolt != null) {
                     smiteBolt.setColor(16776959);
                     smiteBolt.moveTo(livingEntity.position());
                     smiteBolt.setSize((float)(this.position().y - livingEntity.position().y));
                     player.level.addFreshEntity(smiteBolt);
                  }

                  ActiveFlags.IS_AP_ATTACKING.runIfNotSet(() -> ActiveFlags.IS_SMITE_ATTACKING.runIfNotSet(() -> {
                     double damage = AbilityPowerHelper.getAbilityPower(player) * this.percentAbilityPowerDealt;
                     Vec3 delta = livingEntity.getDeltaMovement();
                     livingEntity.hurt(srcPlayerAttack, (float)damage);
                     livingEntity.setDeltaMovement(delta);
                  }));
                  player.level
                     .playSound(
                        null,
                        livingEntity.getX(),
                        livingEntity.getY(),
                        livingEntity.getZ(),
                        ModSounds.SMITE_BOLT,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F + Mth.randomBetween(livingEntity.getRandom(), -0.2F, 0.2F)
                     );
               }
            } else if (VaultStormArrow.StormType.byId((Integer)this.entityData.get(ID_TYPE)) == VaultStormArrow.StormType.BLIZZARD) {
               ArrayList<LivingEntity> result = new ArrayList<>();
               getEntitiesInRange(player.level, this.position(), this.radius, ENTITY_PREDICATE, result, 14.0F);
               result.removeIf(entity -> entity instanceof EternalEntity);
               if (!result.isEmpty()) {
                  LivingEntity livingEntity = result.get(player.level.random.nextInt(result.size()));
                  VaultBlizzardShard shard = (VaultBlizzardShard)ModEntities.BLIZZARD_SHARD.create(player.level);
                  if (shard != null) {
                     Vec3 pos = livingEntity.position();
                     pos = new Vec3(pos.x, this.getY(), pos.z);
                     shard.moveTo(pos);
                     shard.setOwner(this.getOwner());
                     shard.setEntityLocked(livingEntity);
                     shard.setAmplifier(this.getAmplifier());
                     shard.setSlowDuration(this.getSlowDuration());
                     shard.setFrostbiteDuration(this.getFrostbiteDuration());
                     shard.setFrostbiteChance(this.getFrostbiteChance());
                     shard.setIntervalHypothermiaTicks(this.getIntervalHypothermiaTicks());
                     player.level.addFreshEntity(shard);
                  }
               }
            }

            this.intervalTicks = this.intervalTicksMax;
         }

         if (--this.duration < 0) {
            this.discard();
         }
      }
   }

   public static void getEntitiesInRange(
      LevelAccessor levelAccessor, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result, float height
   ) {
      getEntitiesInRange(
         levelAccessor,
         new AABB(
            center.x - (range + 4.0F),
            center.y - (height + 4.0F),
            center.z - (range + 4.0F),
            center.x + (range + 4.0F),
            center.y + (height + 4.0F),
            center.z + (range + 4.0F)
         ),
         center,
         range,
         filter,
         result,
         height
      );
   }

   public static void getEntitiesInRange(
      LevelAccessor levelAccessor, AABB area, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result, float height
   ) {
      if (levelAccessor != null) {
         for (Entity entity : levelAccessor.getEntities((Entity)null, area, filter)) {
            if (isAABBIntersectingOrInsideCylinder(entity, center.x, center.y, center.z, range, height)) {
               result.add((LivingEntity)entity);
            }
         }
      }
   }

   public static double getDistanceInXZ(Vec3 point1, Vec3 point2) {
      double deltaX = point2.x() - point1.x();
      double deltaZ = point2.z() - point1.z();
      return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
   }

   public static boolean isAABBIntersectingOrInsideCylinder(
      Entity entity, double xCylinderCenter, double yCylinderCenter, double zCylinderCenter, double cylinderRadius, double cylinderHeight
   ) {
      if (entity.position().y + entity.getBbHeight() > yCylinderCenter) {
         return false;
      } else {
         return entity.position().y < yCylinderCenter - cylinderHeight
            ? false
            : !(getDistanceInXZ(entity.position(), new Vec3(xCylinderCenter, yCylinderCenter, zCylinderCenter)) > cylinderRadius);
      }
   }

   protected void defineSynchedData() {
      this.getEntityData().define(RADIUS, 5.0F);
      this.getEntityData().define(ID_TYPE, 0);
   }

   protected void doWaterSplashEffect() {
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putInt("Duration", this.duration);
      pCompound.putFloat("Radius", this.radius);
      pCompound.putFloat("PercentAbilityPowerDealt", this.percentAbilityPowerDealt);
      pCompound.putInt("IntervalTicks", this.intervalTicks);
      pCompound.putInt("IntervalTicksMax", this.intervalTicksMax);
      pCompound.putInt("Type", (Integer)this.entityData.get(ID_TYPE));
      pCompound.putInt("slowDuration", this.slowDuration);
      pCompound.putInt("frostbiteDuration", this.frostbiteDuration);
      pCompound.putInt("amplifier", this.amplifier);
      pCompound.putInt("intervalHypothermiaTicks", this.intervalHypothermiaTicks);
      pCompound.putFloat("frostbiteChance", this.frostbiteChance);
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      this.duration = pCompound.getInt("Duration");
      this.radius = pCompound.getFloat("Radius");
      this.percentAbilityPowerDealt = pCompound.getFloat("PercentAbilityPowerDealt");
      this.intervalTicks = pCompound.getInt("IntervalTicks");
      this.intervalTicksMax = pCompound.getInt("IntervalTicksMax");
      this.entityData.set(ID_TYPE, pCompound.getInt("Type"));
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

      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }
   }

   public Packet<?> getAddEntityPacket() {
      Entity entity = this.getOwner();
      return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      Entity entity = this.level.getEntity(pPacket.getData());
      if (entity != null) {
         this.setOwner(entity);
      }
   }

   public static class SmiteBolt extends Entity {
      private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(VaultStormEntity.SmiteBolt.class, EntityDataSerializers.INT);
      private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(VaultStormEntity.SmiteBolt.class, EntityDataSerializers.FLOAT);
      private final boolean flashSky;
      private int life;
      public long seed;
      private int flashes;

      public SmiteBolt(EntityType<? extends VaultStormEntity.SmiteBolt> entityType, Level level, boolean flashSky, int color) {
         super(entityType, level);
         this.flashSky = flashSky;
         this.noCulling = true;
         this.life = 2;
         this.seed = this.random.nextLong();
         this.flashes = this.random.nextInt(3) + 1;
         this.setSize(-1.0F);
         this.setColor(color);
      }

      public void setSize(float size) {
         this.entityData.set(SIZE, size);
      }

      @Nonnull
      public SoundSource getSoundSource() {
         return SoundSource.PLAYERS;
      }

      protected void defineSynchedData() {
         this.entityData.define(COLOR, 0);
         this.entityData.define(SIZE, 0.0F);
      }

      @Nonnull
      public Packet<?> getAddEntityPacket() {
         return new ClientboundAddEntityPacket(this);
      }

      public void setColor(int color) {
         this.entityData.set(COLOR, color);
      }

      public void tick() {
         super.tick();
         this.life--;
         if (this.life < 0) {
            if (this.flashes == 0) {
               this.discard();
            } else if (this.life < -this.random.nextInt(10)) {
               this.flashes--;
               this.life = 1;
               this.seed = this.random.nextLong();
            }
         }

         if (this.life >= 0 && this.flashSky && !(this.level instanceof ServerLevel)) {
            this.level.setSkyFlashTime(2);
         }
      }

      public boolean shouldRenderAtSqrDistance(double distance) {
         double dSqr = 64.0 * getViewScale();
         return distance < dSqr * dSqr;
      }

      protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
      }

      protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SmiteBoltRenderer extends EntityRenderer<VaultStormEntity.SmiteBolt> {
      public SmiteBoltRenderer(Context context) {
         super(context);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull VaultStormEntity.SmiteBolt entity) {
         return TextureAtlas.LOCATION_BLOCKS;
      }

      @ParametersAreNonnullByDefault
      public void render(
         VaultStormEntity.SmiteBolt entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight
      ) {
         float[] afloat = new float[8];
         float[] afloat1 = new float[8];
         float f = 0.0F;
         float f1 = 0.0F;
         float size = (Float)entity.getEntityData().get(VaultStormEntity.SmiteBolt.SIZE);
         if (size != -1.0F) {
            Random random = new Random(entity.seed);

            for (int i = 7; i >= 0; i--) {
               afloat[i] = f;
               afloat1[i] = f1;
               f += random.nextInt(11) - 5;
               f1 += random.nextInt(11) - 5;
            }

            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lightning());
            matrixStack.pushPose();
            matrixStack.scale(0.1F * (size / 1.5F) / 9.0F, 0.1F * size / 13.0F, 0.1F * (size / 1.5F) / 9.0F);
            Matrix4f matrix4f = matrixStack.last().pose();

            for (int j = 0; j < 4; j++) {
               Random random1 = new Random(entity.seed);

               for (int k = 0; k < 3; k++) {
                  int l = 7;
                  int i1 = 0;
                  if (k > 0) {
                     l = 7 - k;
                  }

                  if (k > 0) {
                     i1 = l - 2;
                  }

                  float f2 = afloat[l] - f;
                  float f3 = afloat1[l] - f1;

                  for (int j1 = l; j1 >= i1; j1--) {
                     float f4 = f2;
                     float f5 = f3;
                     if (k == 0) {
                        f2 += random1.nextInt(11) - 5;
                        f3 += random1.nextInt(11) - 5;
                     } else {
                        f2 += random1.nextInt(31) - 15;
                        f3 += random1.nextInt(31) - 15;
                     }

                     float f10 = 0.1F + j * 0.2F;
                     if (k == 0) {
                        f10 *= j1 * 0.1F + 1.0F;
                     }

                     float f11 = 0.1F + j * 0.2F;
                     if (k == 0) {
                        f11 *= (j1 - 1.0F) * 0.1F + 1.0F;
                     }

                     int color = (Integer)entity.getEntityData().get(VaultStormEntity.SmiteBolt.COLOR);
                     float r = (color >>> 16 & 0xFF) / 255.0F;
                     float g = (color >>> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, r, g, b, f10, f11, false, false, true, false);
                     quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, r, g, b, f10, f11, true, false, true, true);
                     quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, r, g, b, f10, f11, true, true, false, true);
                     quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, r, g, b, f10, f11, false, true, false, false);
                  }
               }
            }

            matrixStack.popPose();
         }
      }

      private static void quad(
         Matrix4f matrix,
         VertexConsumer vertexConsumer,
         float x,
         float z,
         int y,
         float p_115278_,
         float p_115279_,
         float r,
         float g,
         float b,
         float p_115283_,
         float p_115284_,
         boolean p_115285_,
         boolean p_115286_,
         boolean p_115287_,
         boolean p_115288_
      ) {
         vertexConsumer.vertex(matrix, x + (p_115285_ ? p_115284_ : -p_115284_), y * 16, z + (p_115286_ ? p_115284_ : -p_115284_))
            .color(r, g, b, 0.3F)
            .endVertex();
         vertexConsumer.vertex(matrix, p_115278_ + (p_115285_ ? p_115283_ : -p_115283_), (y + 1) * 16, p_115279_ + (p_115286_ ? p_115283_ : -p_115283_))
            .color(r, g, b, 0.3F)
            .endVertex();
         vertexConsumer.vertex(matrix, p_115278_ + (p_115287_ ? p_115283_ : -p_115283_), (y + 1) * 16, p_115279_ + (p_115288_ ? p_115283_ : -p_115283_))
            .color(r, g, b, 0.3F)
            .endVertex();
         vertexConsumer.vertex(matrix, x + (p_115287_ ? p_115284_ : -p_115284_), y * 16, z + (p_115288_ ? p_115284_ : -p_115284_))
            .color(r, g, b, 0.3F)
            .endVertex();
      }
   }
}
