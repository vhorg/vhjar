package iskallia.vault.entity.boss;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.CatalystInhibitorItem;
import iskallia.vault.network.message.NovaParticleMessage;
import iskallia.vault.network.message.StonefallParticleMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class BossProtectionCatalystEntity extends Entity {
   public static final EntityDataSerializer<BossProtectionCatalystEntity.CatalystType> TYPE_SERIALIZER = new EntityDataSerializer<BossProtectionCatalystEntity.CatalystType>() {
      public void write(FriendlyByteBuf buf, BossProtectionCatalystEntity.CatalystType value) {
         buf.writeEnum(value);
      }

      public BossProtectionCatalystEntity.CatalystType read(FriendlyByteBuf buf) {
         return (BossProtectionCatalystEntity.CatalystType)buf.readEnum(BossProtectionCatalystEntity.CatalystType.class);
      }

      public BossProtectionCatalystEntity.CatalystType copy(BossProtectionCatalystEntity.CatalystType value) {
         return value;
      }
   };
   private static final EntityDataAccessor<BossProtectionCatalystEntity.CatalystType> TYPE = SynchedEntityData.defineId(
      BossProtectionCatalystEntity.class, TYPE_SERIALIZER
   );
   private static final float RADIUS_AROUND_CENTER = 5.0F;
   private static final float ORBIT_RADIUS = 0.75F;
   private Vec3 center;
   private float angle;
   private float explosionDamageMultiplier;
   private boolean exploding = false;
   private int explosionTimer = 0;
   protected int lerpSteps;
   protected double lerpX;
   protected double lerpY;
   protected double lerpZ;

   public BossProtectionCatalystEntity(
      Level pLevel, Vec3 center, float angle, BossProtectionCatalystEntity.CatalystType catalystType, float explosionDamageMultiplier
   ) {
      this(ModEntities.BOSS_PROTECTION_CATALYST, pLevel);
      this.center = center;
      this.angle = angle;
      this.explosionDamageMultiplier = explosionDamageMultiplier;
      this.setCatalystType(catalystType);
      this.setPos(center);
   }

   public BossProtectionCatalystEntity(EntityType<BossProtectionCatalystEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public void updatePosition() {
      double horizontalAngle = this.angle + Math.toRadians(this.level.getGameTime() % 360L);
      double orbitAngle = horizontalAngle * 7.0;
      double distanceFromCenter = 5.0 + Math.cos(orbitAngle) * 0.75;
      double xAroundCenter = distanceFromCenter * Math.cos(horizontalAngle);
      double zAroundCenter = distanceFromCenter * Math.sin(horizontalAngle);
      Vec3 nextPos = new Vec3(this.center.x() + xAroundCenter, this.center.y() + 2.0 + 0.75 * Math.sin(orbitAngle), this.center.z() + zAroundCenter);
      this.moveTo(nextPos);
   }

   public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {
      this.lerpX = pX;
      this.lerpY = pY;
      this.lerpZ = pZ;
      this.lerpSteps = pPosRotationIncrements;
   }

   protected void defineSynchedData() {
      this.entityData.define(TYPE, BossProtectionCatalystEntity.CatalystType.LIVING);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.lerpSteps > 0) {
            double x = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
            double y = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
            double z = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
            this.lerpSteps--;
            this.setPos(x, y, z);
         }
      } else {
         this.updatePosition();
         if (this.exploding) {
            this.explosionTimer--;
            if (this.explosionTimer <= 0) {
               this.explode();
               this.exploding = false;
            }
         }
      }
   }

   private void explode() {
      this.level.gameEvent(null, GameEvent.EXPLODE, this.getOnPos());
      float radius = 50.0F;
      float diameter = radius * 2.0F;
      int minX = Mth.floor(this.getX() - diameter - 1.0);
      int maxX = Mth.floor(this.getX() + diameter + 1.0);
      int minY = Mth.floor(this.getY() - diameter - 1.0);
      int maxY = Mth.floor(this.getY() + diameter + 1.0);
      int minZ = Mth.floor(this.getZ() - diameter - 1.0);
      int maxZ = Mth.floor(this.getZ() + diameter + 1.0);
      List<Entity> entities = this.level.getEntities(this, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
      Vec3 explosionCenter = new Vec3(this.getX(), this.getY(), this.getZ());
      ModNetwork.CHANNEL
         .send(PacketDistributor.ALL.noArg(), new NovaParticleMessage(new Vec3(this.position().x(), this.position().y(), this.position().z()), 4.0F));
      Map<Player, Vec3> hitPlayers = new HashMap<>();

      for (Entity entity : entities) {
         if (!entity.ignoreExplosion()) {
            double normalizedEntityDist = Math.sqrt(entity.distanceToSqr(explosionCenter)) / diameter;
            if (normalizedEntityDist < 1.0) {
               double xDist = entity.getX() - this.getX();
               double yDist = entity.getEyeY() - this.getY();
               double zDist = entity.getZ() - this.getZ();
               double dist = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
               if (dist != 0.0) {
                  xDist /= dist;
                  yDist /= dist;
                  zDist /= dist;
                  double entityCloseness = 1.0 - normalizedEntityDist;
                  entity.hurt(
                     DamageSource.explosion((LivingEntity)null),
                     (int)((entityCloseness * entityCloseness + entityCloseness) / 2.0 * this.explosionDamageMultiplier * diameter + 1.0)
                  );
                  double knockbackRatio = entityCloseness;
                  if (entity instanceof LivingEntity livingEntity) {
                     knockbackRatio = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, entityCloseness);
                  }

                  entity.setDeltaMovement(entity.getDeltaMovement().add(xDist * knockbackRatio, yDist * knockbackRatio, zDist * knockbackRatio));
                  if (entity instanceof Player) {
                     Player player = (Player)entity;
                     if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        hitPlayers.put(player, new Vec3(xDist * entityCloseness, yDist * entityCloseness, zDist * entityCloseness));
                     }
                  }
               }
            }
         }
      }

      if (this.level instanceof ServerLevel serverLevel) {
         for (ServerPlayer serverplayer : serverLevel.players()) {
            if (serverplayer.distanceToSqr(this.position()) < 4096.0) {
               serverplayer.connection
                  .send(new ClientboundExplodePacket(this.getX(), this.getY(), this.getZ(), 50.0F, List.of(), hitPlayers.get(serverplayer)));
            }
         }
      }
   }

   public void hitWithInhibitor(Player player, ItemStack inhibitorStack) {
      if (inhibitorStack.getItem() instanceof CatalystInhibitorItem catalystInhibitorItem) {
         if (this.getCatalystType() == catalystInhibitorItem.getCatalystType()) {
            if (!this.level.isClientSide) {
               this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
               ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
               ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(), new StonefallParticleMessage(new Vec3(this.position().x(), this.position().y(), this.position().z()), 3.0F)
                  );
               this.level
                  .playSound(
                     null,
                     this.position().x(),
                     this.position().y(),
                     this.position().z(),
                     ModSounds.ARTIFACT_BOSS_CATALYST_HIT,
                     SoundSource.BLOCKS,
                     1.0F,
                     0.75F + new Random().nextFloat() * 0.35F
                  );
               this.discard();
            }
         } else {
            this.level
               .playSound(
                  null,
                  this.position().x(),
                  this.position().y(),
                  this.position().z(),
                  ModSounds.ARTIFACT_BOSS_CATALYST_HIT_WRONG,
                  SoundSource.BLOCKS,
                  1.0F,
                  0.75F + new Random().nextFloat() * 0.35F
               );
            this.exploding = true;
            this.explosionTimer = 20;
         }
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      CompoundTag centerTag = tag.getCompound("Center");
      this.center = new Vec3(centerTag.getDouble("X"), centerTag.getDouble("Y"), centerTag.getDouble("Z"));
      this.angle = tag.getFloat("Angle");
      this.explosionDamageMultiplier = tag.getFloat("ExplosionDamageMultiplier");
      this.setCatalystType(BossProtectionCatalystEntity.CatalystType.valueOf(tag.getString("CatalystType")));
   }

   public void setCatalystType(BossProtectionCatalystEntity.CatalystType catalystType) {
      this.entityData.set(TYPE, catalystType);
   }

   public BossProtectionCatalystEntity.CatalystType getCatalystType() {
      return (BossProtectionCatalystEntity.CatalystType)this.entityData.get(TYPE);
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      CompoundTag centerTag = new CompoundTag();
      centerTag.putDouble("X", this.center.x);
      centerTag.putDouble("Y", this.center.y);
      centerTag.putDouble("Z", this.center.z);
      tag.put("Center", centerTag);
      tag.putFloat("Angle", this.angle);
      tag.putFloat("ExplosionDamageMultiplier", this.explosionDamageMultiplier);
      tag.putString("CatalystType", this.getCatalystType().name());
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   static {
      EntityDataSerializers.registerSerializer(TYPE_SERIALIZER);
   }

   public static enum CatalystType {
      LIVING,
      WOODEN,
      ORNATE,
      GILDED;
   }
}
