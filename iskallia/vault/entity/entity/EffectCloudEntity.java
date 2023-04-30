package iskallia.vault.entity.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import iskallia.vault.world.data.VaultPartyData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectCloudEntity extends Entity {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(EffectCloudEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EffectCloudEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> IGNORE_RADIUS = SynchedEntityData.defineId(EffectCloudEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<ParticleOptions> PARTICLE = SynchedEntityData.defineId(EffectCloudEntity.class, EntityDataSerializers.PARTICLE);
   private Potion potion = Potions.EMPTY;
   private final List<MobEffectInstance> effects = Lists.newArrayList();
   private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
   private int duration = 600;
   private int waitTime = 20;
   private int reapplicationDelay = 20;
   private boolean affectsOwner = true;
   private boolean colorSet;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private LivingEntity owner;
   private UUID ownerUniqueId;

   public EffectCloudEntity(EntityType<? extends EffectCloudEntity> cloud, Level world) {
      super(cloud, world);
      this.noPhysics = true;
      this.setRadius(3.0F);
   }

   public EffectCloudEntity(Level world, double x, double y, double z) {
      this(ModEntities.EFFECT_CLOUD, world);
      this.setPos(x, y, z);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(COLOR, 0);
      this.getEntityData().define(RADIUS, 0.5F);
      this.getEntityData().define(IGNORE_RADIUS, false);
      this.getEntityData().define(PARTICLE, (ParticleOptions)ModParticles.CLOUD_EFFECT.get());
   }

   public void setRadius(float radiusIn) {
      if (!this.level.isClientSide) {
         this.getEntityData().set(RADIUS, radiusIn);
      }
   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   public float getRadius() {
      return (Float)this.getEntityData().get(RADIUS);
   }

   public void setPotion(Potion potionIn) {
      this.potion = potionIn;
      if (!this.colorSet) {
         this.updateFixedColor();
      }
   }

   private void updateFixedColor() {
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getEntityData().set(COLOR, 0);
      } else {
         this.getEntityData().set(COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
      }
   }

   public void addEffect(MobEffectInstance effect) {
      this.effects.add(effect);
      if (!this.colorSet) {
         this.updateFixedColor();
      }
   }

   public int getColor() {
      return (Integer)this.getEntityData().get(COLOR);
   }

   public void setColor(int colorIn) {
      this.colorSet = true;
      this.getEntityData().set(COLOR, colorIn);
   }

   public boolean affectsOwner() {
      return this.affectsOwner;
   }

   public void setAffectsOwner(boolean affectsOwner) {
      this.affectsOwner = affectsOwner;
   }

   public ParticleOptions getParticleData() {
      return (ParticleOptions)this.getEntityData().get(PARTICLE);
   }

   public void setParticleData(ParticleOptions particleData) {
      this.getEntityData().set(PARTICLE, particleData);
   }

   protected void setIgnoreRadius(boolean ignoreRadius) {
      this.getEntityData().set(IGNORE_RADIUS, ignoreRadius);
   }

   public boolean shouldIgnoreRadius() {
      return (Boolean)this.getEntityData().get(IGNORE_RADIUS);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int durationIn) {
      this.duration = durationIn;
   }

   public void setRemainingFireTicks(int seconds) {
      super.setRemainingFireTicks(0);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         this.tickParticles();
      } else {
         boolean ignoreRadius = this.shouldIgnoreRadius();
         float radius = this.getRadius();
         if (this.tickCount >= this.waitTime + this.duration) {
            this.remove(RemovalReason.DISCARDED);
            return;
         }

         boolean flag1 = this.tickCount < this.waitTime;
         if (ignoreRadius != flag1) {
            this.setIgnoreRadius(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            radius += this.radiusPerTick;
            if (radius < 0.5F) {
               this.remove(RemovalReason.DISCARDED);
               return;
            }

            this.setRadius(radius);
         }

         if (this.tickCount % 5 == 0) {
            this.reapplicationDelayMap.entrySet().removeIf(entry -> this.tickCount >= entry.getValue());
            List<MobEffectInstance> effectsToApply = Lists.newArrayList();

            for (MobEffectInstance effect : this.potion.getEffects()) {
               effectsToApply.add(
                  new MobEffectInstance(effect.getEffect(), effect.getDuration() / 4, effect.getAmplifier(), effect.isAmbient(), effect.isVisible())
               );
            }

            effectsToApply.addAll(this.effects);
            if (effectsToApply.isEmpty()) {
               this.reapplicationDelayMap.clear();
            } else {
               List<LivingEntity> entitiesInRadius = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if (!entitiesInRadius.isEmpty()) {
                  for (LivingEntity livingentity : entitiesInRadius) {
                     if (this.canApplyEffects(livingentity) && !this.reapplicationDelayMap.containsKey(livingentity) && livingentity.isAffectedByPotions()) {
                        double xDiff = livingentity.getX() - this.getX();
                        double zDiff = livingentity.getZ() - this.getZ();
                        double distance = xDiff * xDiff + zDiff * zDiff;
                        if (distance <= radius * radius) {
                           this.reapplicationDelayMap.put(livingentity, this.tickCount + this.reapplicationDelay);

                           for (MobEffectInstance effectinstance : effectsToApply) {
                              if (effectinstance.getEffect().isInstantenous()) {
                                 ActiveFlags.IS_AOE_ATTACKING
                                    .runIfNotSet(
                                       () -> effectinstance.getEffect()
                                          .applyInstantenousEffect(this, this.getOwner(), livingentity, effectinstance.getAmplifier(), 0.5)
                                    );
                              } else {
                                 livingentity.addEffect(new MobEffectInstance(effectinstance));
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              radius += this.radiusOnUse;
                              if (radius < 0.5F) {
                                 this.remove(RemovalReason.DISCARDED);
                                 return;
                              }

                              this.setRadius(radius);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration = this.duration + this.durationOnUse;
                              if (this.duration <= 0) {
                                 this.remove(RemovalReason.DISCARDED);
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void tickParticles() {
      boolean ignoreRadius = this.shouldIgnoreRadius();
      float radius = this.getRadius();
      ParticleOptions iparticledata = this.getParticleData();
      if (ignoreRadius) {
         if (this.random.nextBoolean()) {
            for (int i = 0; i < 2; i++) {
               float randomRad = this.random.nextFloat() * (float) (Math.PI * 2);
               float randomDst = Mth.sqrt(this.random.nextFloat()) * 0.2F;
               float xOffset = Mth.cos(randomRad) * randomDst;
               float zOffset = Mth.sin(randomRad) * randomDst;
               if (this.random.nextInt(25) == 0) {
                  if (iparticledata.getType() != ParticleTypes.ENTITY_EFFECT && iparticledata.getType() != ModParticles.CLOUD_EFFECT.get()) {
                     this.level.addAlwaysVisibleParticle(iparticledata, this.getX() + xOffset, this.getY(), this.getZ() + zOffset, 0.0, 0.0, 0.0);
                  } else {
                     int color = this.random.nextBoolean() ? 16777215 : this.getColor();
                     int r = color >> 16 & 0xFF;
                     int g = color >> 8 & 0xFF;
                     int b = color & 0xFF;
                     this.level
                        .addAlwaysVisibleParticle(iparticledata, this.getX() + xOffset, this.getY(), this.getZ() + zOffset, r / 255.0F, g / 255.0F, b / 255.0F);
                  }
               }
            }
         }
      } else {
         float distance = (float) Math.PI * radius * radius;

         for (int ix = 0; ix < distance; ix++) {
            float randomRad = this.random.nextFloat() * (float) (Math.PI * 2);
            float randomDst = Mth.sqrt(this.random.nextFloat()) * radius;
            float xOffset = Mth.cos(randomRad) * randomDst;
            float zOffset = Mth.sin(randomRad) * randomDst;
            if (this.random.nextInt(25) == 0) {
               if (iparticledata.getType() != ParticleTypes.ENTITY_EFFECT && iparticledata.getType() != ModParticles.CLOUD_EFFECT.get()) {
                  this.level
                     .addAlwaysVisibleParticle(
                        iparticledata,
                        this.getX() + xOffset,
                        this.getY(),
                        this.getZ() + zOffset,
                        (0.5 - this.random.nextDouble()) * 0.15,
                        0.01F,
                        (0.5 - this.random.nextDouble()) * 0.15
                     );
               } else {
                  int color = this.getColor();
                  int r = color >> 16 & 0xFF;
                  int g = color >> 8 & 0xFF;
                  int b = color & 0xFF;
                  this.level
                     .addAlwaysVisibleParticle(iparticledata, this.getX() + xOffset, this.getY(), this.getZ() + zOffset, r / 255.0F, g / 255.0F, b / 255.0F);
               }
            }
         }
      }
   }

   protected boolean canApplyEffects(LivingEntity target) {
      if (!this.affectsOwner()) {
         if (this.ownerUniqueId == null) {
            return true;
         }

         UUID targetUUID = target.getUUID();
         if (targetUUID.equals(this.ownerUniqueId)) {
            return false;
         }

         UUID ownerUUID = this.ownerUniqueId;
         if (!(this.getCommandSenderWorld() instanceof ServerLevel sWorld)) {
            return true;
         }

         LivingEntity owner = this.getOwner();
         if (owner instanceof EternalEntity) {
            UUID eternalOwnerUUID = (UUID)((EternalEntity)owner).getOwner().map(Function.identity(), Entity::getUUID);
            if (targetUUID.equals(eternalOwnerUUID)) {
               return false;
            }

            VaultPartyData.Party party = VaultPartyData.get(sWorld).getParty(eternalOwnerUUID).orElse(null);
            if (party != null && party.hasMember(targetUUID)) {
               return false;
            }

            ownerUUID = eternalOwnerUUID;
         }

         if (target instanceof EternalEntity) {
            UUID eternalTargetOwnerUUID = (UUID)((EternalEntity)target).getOwner().map(Function.identity(), Entity::getUUID);
            if (eternalTargetOwnerUUID.equals(ownerUUID)) {
               return false;
            }

            VaultPartyData.Party party = VaultPartyData.get(sWorld).getParty(eternalTargetOwnerUUID).orElse(null);
            if (party != null && party.hasMember(ownerUUID)) {
               return false;
            }
         }

         if (target instanceof TamableAnimal tamable && ownerUUID.equals(tamable.getOwnerUUID())) {
            return false;
         }

         VaultPartyData.Party party = VaultPartyData.get(sWorld).getParty(ownerUUID).orElse(null);
         if (party != null && party.hasMember(targetUUID)) {
            return false;
         }
      }

      return true;
   }

   public void setRadiusOnUse(float radiusOnUseIn) {
      this.radiusOnUse = radiusOnUseIn;
   }

   public void setRadiusPerTick(float radiusPerTickIn) {
      this.radiusPerTick = radiusPerTickIn;
   }

   public void setWaitTime(int waitTimeIn) {
      this.waitTime = waitTimeIn;
   }

   public void setOwner(@Nullable LivingEntity ownerIn) {
      this.owner = ownerIn;
      this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUniqueId);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.tickCount = compound.getInt("Age");
      this.duration = compound.getInt("Duration");
      this.waitTime = compound.getInt("WaitTime");
      this.reapplicationDelay = compound.getInt("ReapplicationDelay");
      this.durationOnUse = compound.getInt("DurationOnUse");
      this.radiusOnUse = compound.getFloat("RadiusOnUse");
      this.radiusPerTick = compound.getFloat("RadiusPerTick");
      this.setRadius(compound.getFloat("Radius"));
      if (compound.hasUUID("Owner")) {
         this.ownerUniqueId = compound.getUUID("Owner");
      }

      if (compound.contains("Particle", 8)) {
         try {
            this.setParticleData(ParticleArgument.readParticle(new StringReader(compound.getString("Particle"))));
         } catch (CommandSyntaxException var5) {
            PRIVATE_LOGGER.warn("Couldn't load custom particle {}", compound.getString("Particle"), var5);
         }
      }

      if (compound.contains("Color", 99)) {
         this.setColor(compound.getInt("Color"));
      }

      if (compound.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotion(compound));
      }

      if (compound.contains("Effects", 9)) {
         ListTag listnbt = compound.getList("Effects", 10);
         this.effects.clear();

         for (int i = 0; i < listnbt.size(); i++) {
            MobEffectInstance effectinstance = MobEffectInstance.load(listnbt.getCompound(i));
            if (effectinstance != null) {
               this.addEffect(effectinstance);
            }
         }
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.tickCount);
      compound.putInt("Duration", this.duration);
      compound.putInt("WaitTime", this.waitTime);
      compound.putInt("ReapplicationDelay", this.reapplicationDelay);
      compound.putInt("DurationOnUse", this.durationOnUse);
      compound.putFloat("RadiusOnUse", this.radiusOnUse);
      compound.putFloat("RadiusPerTick", this.radiusPerTick);
      compound.putFloat("Radius", this.getRadius());
      compound.putString("Particle", this.getParticleData().writeToString());
      if (this.ownerUniqueId != null) {
         compound.putUUID("Owner", this.ownerUniqueId);
      }

      if (this.colorSet) {
         compound.putInt("Color", this.getColor());
      }

      if (this.potion != Potions.EMPTY && this.potion != null) {
         compound.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListTag listnbt = new ListTag();

         for (MobEffectInstance effectinstance : this.effects) {
            listnbt.add(effectinstance.save(new CompoundTag()));
         }

         compound.put("Effects", listnbt);
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      if (RADIUS.equals(key)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(key);
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public EntityDimensions getDimensions(Pose poseIn) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }

   public static EffectCloudEntity fromConfig(Level world, LivingEntity owner, double x, double y, double z, EffectCloudEntity.Config config) {
      EffectCloudEntity cloud = new EffectCloudEntity(world, x, y, z);
      cloud.setPotion(config.getPotion());
      config.getEffects().forEach(effect -> cloud.addEffect(effect.create()));
      if (config.getDuration() >= 0) {
         cloud.setDuration(config.getDuration());
      }

      if (config.getRadius() >= 0.0F) {
         cloud.setRadius(config.getRadius());
      }

      if (config.getColor() >= 0) {
         cloud.setColor(config.getColor());
      }

      cloud.setAffectsOwner(config.affectsOwner());
      cloud.setOwner(owner);
      return cloud;
   }

   public static class Config implements INBTSerializable<CompoundTag> {
      @Expose
      private String name;
      @Expose
      private String potion;
      @Expose
      private List<EffectCloudEntity.Config.CloudEffect> effects;
      @Expose
      private int duration;
      @Expose
      private float radius;
      @Expose
      private int color;
      @Expose
      private boolean affectsOwner;
      @Expose
      private float chance;

      @Override
      public String toString() {
         return "Config{name='" + this.name + "', potion='" + this.potion + "', effects=" + this.effects + "}";
      }

      public Config() {
         this("Dummy", Potions.EMPTY, new ArrayList<>(), 600, 3.0F, -1, true, 1.0F);
      }

      public Config(
         String name,
         Potion potion,
         List<EffectCloudEntity.Config.CloudEffect> effects,
         int duration,
         float radius,
         int color,
         boolean affectsOwner,
         float chance
      ) {
         this.name = name;
         this.potion = potion.getRegistryName().toString();
         this.effects = effects;
         this.duration = duration;
         this.radius = radius;
         this.color = color;
         this.affectsOwner = affectsOwner;
         this.chance = chance;
      }

      public static EffectCloudEntity.Config fromNBT(CompoundTag nbt) {
         EffectCloudEntity.Config config = new EffectCloudEntity.Config();
         config.deserializeNBT(nbt);
         return config;
      }

      public String getName() {
         return this.name;
      }

      public Potion getPotion() {
         return Registry.POTION.getOptional(new ResourceLocation(this.potion)).orElse(Potions.EMPTY);
      }

      public List<EffectCloudEntity.Config.CloudEffect> getEffects() {
         return this.effects;
      }

      public int getDuration() {
         return this.duration;
      }

      public float getRadius() {
         return this.radius;
      }

      public int getColor() {
         return this.color;
      }

      public boolean affectsOwner() {
         return this.affectsOwner;
      }

      public float getChance() {
         return this.chance;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("Name", this.name);
         nbt.putString("Potion", this.potion);
         ListTag effectsList = new ListTag();
         this.effects.forEach(cloudEffect -> effectsList.add(cloudEffect.serializeNBT()));
         nbt.put("Effects", effectsList);
         nbt.putInt("Duration", this.duration);
         nbt.putFloat("Radius", this.radius);
         nbt.putInt("Color", this.color);
         nbt.putBoolean("AffectsOwner", this.affectsOwner);
         nbt.putFloat("Chance", this.chance);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.name = nbt.getString("Name");
         this.potion = nbt.getString("Potion");
         ListTag effectsList = nbt.getList("Effects", 10);
         this.effects = effectsList.stream().map(inbt -> EffectCloudEntity.Config.CloudEffect.fromNBT((CompoundTag)inbt)).collect(Collectors.toList());
         this.duration = nbt.getInt("Duration");
         this.radius = nbt.getFloat("Radius");
         this.color = nbt.getInt("Color");
         this.affectsOwner = nbt.getBoolean("AffectsOwner");
         this.chance = nbt.getFloat("Chance");
      }

      public static class CloudEffect implements INBTSerializable<CompoundTag> {
         @Expose
         private String effect;
         @Expose
         private int duration;
         @Expose
         private int amplifier;
         @Expose
         private boolean showParticles;
         @Expose
         private boolean showIcon;

         protected CloudEffect() {
         }

         public CloudEffect(MobEffect effect, int duration, int amplifier, boolean showParticles, boolean showIcon) {
            this.effect = effect.getRegistryName().toString();
            this.duration = duration;
            this.amplifier = amplifier;
            this.showParticles = false;
            this.showIcon = true;
         }

         public static EffectCloudEntity.Config.CloudEffect fromNBT(CompoundTag nbt) {
            EffectCloudEntity.Config.CloudEffect effect = new EffectCloudEntity.Config.CloudEffect();
            effect.deserializeNBT(nbt);
            return effect;
         }

         public MobEffect getEffect() {
            return (MobEffect)Registry.MOB_EFFECT.get(new ResourceLocation(this.effect));
         }

         public int getDuration() {
            return this.duration;
         }

         public int getAmplifier() {
            return this.amplifier;
         }

         public boolean showParticles() {
            return this.showParticles;
         }

         public boolean showIcon() {
            return this.showIcon;
         }

         public MobEffectInstance create() {
            return new MobEffectInstance(this.getEffect(), this.getDuration(), this.getAmplifier(), false, this.showParticles(), this.showIcon());
         }

         public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Effect", this.effect);
            nbt.putInt("Duration", this.duration);
            nbt.putInt("Amplifier", this.amplifier);
            nbt.putBoolean("ShowParticles", this.showParticles);
            nbt.putBoolean("ShowIcon", this.showIcon);
            return nbt;
         }

         public void deserializeNBT(CompoundTag nbt) {
            this.effect = nbt.getString("Effect");
            this.duration = nbt.getInt("Duration");
            this.amplifier = nbt.getInt("Amplifier");
            this.showParticles = nbt.getBoolean("ShowParticles");
            this.showIcon = nbt.getBoolean("ShowIcon");
         }
      }
   }
}
