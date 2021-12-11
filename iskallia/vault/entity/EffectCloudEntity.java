package iskallia.vault.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.VaultPartyData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectCloudEntity extends Entity {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<Float> RADIUS = EntityDataManager.func_187226_a(EffectCloudEntity.class, DataSerializers.field_187193_c);
   private static final DataParameter<Integer> COLOR = EntityDataManager.func_187226_a(EffectCloudEntity.class, DataSerializers.field_187192_b);
   private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.func_187226_a(EffectCloudEntity.class, DataSerializers.field_187198_h);
   private static final DataParameter<IParticleData> PARTICLE = EntityDataManager.func_187226_a(EffectCloudEntity.class, DataSerializers.field_198166_i);
   private Potion potion = Potions.field_185229_a;
   private final List<EffectInstance> effects = Lists.newArrayList();
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

   public EffectCloudEntity(EntityType<? extends EffectCloudEntity> cloud, World world) {
      super(cloud, world);
      this.field_70145_X = true;
      this.setRadius(3.0F);
   }

   public EffectCloudEntity(World world, double x, double y, double z) {
      this(ModEntities.EFFECT_CLOUD, world);
      this.func_70107_b(x, y, z);
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(COLOR, 0);
      this.func_184212_Q().func_187214_a(RADIUS, 0.5F);
      this.func_184212_Q().func_187214_a(IGNORE_RADIUS, false);
      this.func_184212_Q().func_187214_a(PARTICLE, ParticleTypes.field_197625_r);
   }

   public void setRadius(float radiusIn) {
      if (!this.field_70170_p.field_72995_K) {
         this.func_184212_Q().func_187227_b(RADIUS, radiusIn);
      }
   }

   public void func_213323_x_() {
      double d0 = this.func_226277_ct_();
      double d1 = this.func_226278_cu_();
      double d2 = this.func_226281_cx_();
      super.func_213323_x_();
      this.func_70107_b(d0, d1, d2);
   }

   public float getRadius() {
      return (Float)this.func_184212_Q().func_187225_a(RADIUS);
   }

   public void setPotion(Potion potionIn) {
      this.potion = potionIn;
      if (!this.colorSet) {
         this.updateFixedColor();
      }
   }

   private void updateFixedColor() {
      if (this.potion == Potions.field_185229_a && this.effects.isEmpty()) {
         this.func_184212_Q().func_187227_b(COLOR, 0);
      } else {
         this.func_184212_Q().func_187227_b(COLOR, PotionUtils.func_185181_a(PotionUtils.func_185186_a(this.potion, this.effects)));
      }
   }

   public void addEffect(EffectInstance effect) {
      this.effects.add(effect);
      if (!this.colorSet) {
         this.updateFixedColor();
      }
   }

   public int getColor() {
      return (Integer)this.func_184212_Q().func_187225_a(COLOR);
   }

   public void setColor(int colorIn) {
      this.colorSet = true;
      this.func_184212_Q().func_187227_b(COLOR, colorIn);
   }

   public boolean affectsOwner() {
      return this.affectsOwner;
   }

   private void setAffectsOwner(boolean affectsOwner) {
      this.affectsOwner = affectsOwner;
   }

   public IParticleData getParticleData() {
      return (IParticleData)this.func_184212_Q().func_187225_a(PARTICLE);
   }

   public void setParticleData(IParticleData particleData) {
      this.func_184212_Q().func_187227_b(PARTICLE, particleData);
   }

   protected void setIgnoreRadius(boolean ignoreRadius) {
      this.func_184212_Q().func_187227_b(IGNORE_RADIUS, ignoreRadius);
   }

   public boolean shouldIgnoreRadius() {
      return (Boolean)this.func_184212_Q().func_187225_a(IGNORE_RADIUS);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int durationIn) {
      this.duration = durationIn;
   }

   public void func_241209_g_(int seconds) {
      super.func_241209_g_(0);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         this.tickParticles();
      } else {
         boolean ignoreRadius = this.shouldIgnoreRadius();
         float radius = this.getRadius();
         if (this.field_70173_aa >= this.waitTime + this.duration) {
            this.func_70106_y();
            return;
         }

         boolean flag1 = this.field_70173_aa < this.waitTime;
         if (ignoreRadius != flag1) {
            this.setIgnoreRadius(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            radius += this.radiusPerTick;
            if (radius < 0.5F) {
               this.func_70106_y();
               return;
            }

            this.setRadius(radius);
         }

         if (this.field_70173_aa % 5 == 0) {
            this.reapplicationDelayMap.entrySet().removeIf(entry -> this.field_70173_aa >= entry.getValue());
            List<EffectInstance> effectsToApply = Lists.newArrayList();

            for (EffectInstance effect : this.potion.func_185170_a()) {
               effectsToApply.add(
                  new EffectInstance(effect.func_188419_a(), effect.func_76459_b() / 4, effect.func_76458_c(), effect.func_82720_e(), effect.func_188418_e())
               );
            }

            effectsToApply.addAll(this.effects);
            if (effectsToApply.isEmpty()) {
               this.reapplicationDelayMap.clear();
            } else {
               List<LivingEntity> entitiesInRadius = this.field_70170_p.func_217357_a(LivingEntity.class, this.func_174813_aQ());
               if (!entitiesInRadius.isEmpty()) {
                  for (LivingEntity livingentity : entitiesInRadius) {
                     if (this.canApplyEffects(livingentity) && !this.reapplicationDelayMap.containsKey(livingentity) && livingentity.func_184603_cC()) {
                        double xDiff = livingentity.func_226277_ct_() - this.func_226277_ct_();
                        double zDiff = livingentity.func_226281_cx_() - this.func_226281_cx_();
                        double distance = xDiff * xDiff + zDiff * zDiff;
                        if (distance <= radius * radius) {
                           this.reapplicationDelayMap.put(livingentity, this.field_70173_aa + this.reapplicationDelay);

                           for (EffectInstance effectinstance : effectsToApply) {
                              if (effectinstance.func_188419_a().func_76403_b()) {
                                 ActiveFlags.IS_AOE_ATTACKING
                                    .runIfNotSet(
                                       () -> effectinstance.func_188419_a()
                                          .func_180793_a(this, this.getOwner(), livingentity, effectinstance.func_76458_c(), 0.5)
                                    );
                              } else {
                                 livingentity.func_195064_c(new EffectInstance(effectinstance));
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              radius += this.radiusOnUse;
                              if (radius < 0.5F) {
                                 this.func_70106_y();
                                 return;
                              }

                              this.setRadius(radius);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration = this.duration + this.durationOnUse;
                              if (this.duration <= 0) {
                                 this.func_70106_y();
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
      IParticleData iparticledata = this.getParticleData();
      if (ignoreRadius) {
         if (this.field_70146_Z.nextBoolean()) {
            for (int i = 0; i < 2; i++) {
               float randomRad = this.field_70146_Z.nextFloat() * (float) (Math.PI * 2);
               float randomDst = MathHelper.func_76129_c(this.field_70146_Z.nextFloat()) * 0.2F;
               float xOffset = MathHelper.func_76134_b(randomRad) * randomDst;
               float zOffset = MathHelper.func_76126_a(randomRad) * randomDst;
               if (iparticledata.func_197554_b() == ParticleTypes.field_197625_r) {
                  int color = this.field_70146_Z.nextBoolean() ? 16777215 : this.getColor();
                  int r = color >> 16 & 0xFF;
                  int g = color >> 8 & 0xFF;
                  int b = color & 0xFF;
                  this.field_70170_p
                     .func_195589_b(
                        iparticledata,
                        this.func_226277_ct_() + xOffset,
                        this.func_226278_cu_(),
                        this.func_226281_cx_() + zOffset,
                        r / 255.0F,
                        g / 255.0F,
                        b / 255.0F
                     );
               } else {
                  this.field_70170_p
                     .func_195589_b(iparticledata, this.func_226277_ct_() + xOffset, this.func_226278_cu_(), this.func_226281_cx_() + zOffset, 0.0, 0.0, 0.0);
               }
            }
         }
      } else {
         float distance = (float) Math.PI * radius * radius;

         for (int ix = 0; ix < distance; ix++) {
            float randomRad = this.field_70146_Z.nextFloat() * (float) (Math.PI * 2);
            float randomDst = MathHelper.func_76129_c(this.field_70146_Z.nextFloat()) * radius;
            float xOffset = MathHelper.func_76134_b(randomRad) * randomDst;
            float zOffset = MathHelper.func_76126_a(randomRad) * randomDst;
            if (iparticledata.func_197554_b() == ParticleTypes.field_197625_r) {
               int color = this.getColor();
               int r = color >> 16 & 0xFF;
               int g = color >> 8 & 0xFF;
               int b = color & 0xFF;
               this.field_70170_p
                  .func_195589_b(
                     iparticledata,
                     this.func_226277_ct_() + xOffset,
                     this.func_226278_cu_(),
                     this.func_226281_cx_() + zOffset,
                     r / 255.0F,
                     g / 255.0F,
                     b / 255.0F
                  );
            } else {
               this.field_70170_p
                  .func_195589_b(
                     iparticledata,
                     this.func_226277_ct_() + xOffset,
                     this.func_226278_cu_(),
                     this.func_226281_cx_() + zOffset,
                     (0.5 - this.field_70146_Z.nextDouble()) * 0.15,
                     0.01F,
                     (0.5 - this.field_70146_Z.nextDouble()) * 0.15
                  );
            }
         }
      }
   }

   protected boolean canApplyEffects(LivingEntity target) {
      if (!this.affectsOwner()) {
         if (this.ownerUniqueId == null) {
            return true;
         }

         UUID targetUUID = target.func_110124_au();
         if (targetUUID.equals(this.ownerUniqueId)) {
            return false;
         }

         UUID ownerUUID = this.ownerUniqueId;
         World world = this.func_130014_f_();
         if (!(world instanceof ServerWorld)) {
            return true;
         }

         ServerWorld sWorld = (ServerWorld)world;
         LivingEntity owner = this.getOwner();
         if (owner instanceof EternalEntity) {
            UUID eternalOwnerUUID = (UUID)((EternalEntity)owner).getOwner().map(Function.identity(), Entity::func_110124_au);
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
            UUID eternalTargetOwnerUUID = (UUID)((EternalEntity)target).getOwner().map(Function.identity(), Entity::func_110124_au);
            if (eternalTargetOwnerUUID.equals(ownerUUID)) {
               return false;
            }

            VaultPartyData.Party party = VaultPartyData.get(sWorld).getParty(eternalTargetOwnerUUID).orElse(null);
            if (party != null && party.hasMember(ownerUUID)) {
               return false;
            }
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
      this.ownerUniqueId = ownerIn == null ? null : ownerIn.func_110124_au();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.field_70170_p instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.field_70170_p).func_217461_a(this.ownerUniqueId);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   protected void func_70037_a(CompoundNBT compound) {
      this.field_70173_aa = compound.func_74762_e("Age");
      this.duration = compound.func_74762_e("Duration");
      this.waitTime = compound.func_74762_e("WaitTime");
      this.reapplicationDelay = compound.func_74762_e("ReapplicationDelay");
      this.durationOnUse = compound.func_74762_e("DurationOnUse");
      this.radiusOnUse = compound.func_74760_g("RadiusOnUse");
      this.radiusPerTick = compound.func_74760_g("RadiusPerTick");
      this.setRadius(compound.func_74760_g("Radius"));
      if (compound.func_186855_b("Owner")) {
         this.ownerUniqueId = compound.func_186857_a("Owner");
      }

      if (compound.func_150297_b("Particle", 8)) {
         try {
            this.setParticleData(ParticleArgument.func_197189_a(new StringReader(compound.func_74779_i("Particle"))));
         } catch (CommandSyntaxException var5) {
            PRIVATE_LOGGER.warn("Couldn't load custom particle {}", compound.func_74779_i("Particle"), var5);
         }
      }

      if (compound.func_150297_b("Color", 99)) {
         this.setColor(compound.func_74762_e("Color"));
      }

      if (compound.func_150297_b("Potion", 8)) {
         this.setPotion(PotionUtils.func_185187_c(compound));
      }

      if (compound.func_150297_b("Effects", 9)) {
         ListNBT listnbt = compound.func_150295_c("Effects", 10);
         this.effects.clear();

         for (int i = 0; i < listnbt.size(); i++) {
            EffectInstance effectinstance = EffectInstance.func_82722_b(listnbt.func_150305_b(i));
            if (effectinstance != null) {
               this.addEffect(effectinstance);
            }
         }
      }
   }

   protected void func_213281_b(CompoundNBT compound) {
      compound.func_74768_a("Age", this.field_70173_aa);
      compound.func_74768_a("Duration", this.duration);
      compound.func_74768_a("WaitTime", this.waitTime);
      compound.func_74768_a("ReapplicationDelay", this.reapplicationDelay);
      compound.func_74768_a("DurationOnUse", this.durationOnUse);
      compound.func_74776_a("RadiusOnUse", this.radiusOnUse);
      compound.func_74776_a("RadiusPerTick", this.radiusPerTick);
      compound.func_74776_a("Radius", this.getRadius());
      compound.func_74778_a("Particle", this.getParticleData().func_197555_a());
      if (this.ownerUniqueId != null) {
         compound.func_186854_a("Owner", this.ownerUniqueId);
      }

      if (this.colorSet) {
         compound.func_74768_a("Color", this.getColor());
      }

      if (this.potion != Potions.field_185229_a && this.potion != null) {
         compound.func_74778_a("Potion", Registry.field_212621_j.func_177774_c(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListNBT listnbt = new ListNBT();

         for (EffectInstance effectinstance : this.effects) {
            listnbt.add(effectinstance.func_82719_a(new CompoundNBT()));
         }

         compound.func_218657_a("Effects", listnbt);
      }
   }

   public void func_184206_a(DataParameter<?> key) {
      if (RADIUS.equals(key)) {
         this.func_213323_x_();
      }

      super.func_184206_a(key);
   }

   public PushReaction func_184192_z() {
      return PushReaction.IGNORE;
   }

   public IPacket<?> func_213297_N() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public EntitySize func_213305_a(Pose poseIn) {
      return EntitySize.func_220314_b(this.getRadius() * 2.0F, 0.5F);
   }

   public static EffectCloudEntity fromConfig(World world, LivingEntity owner, double x, double y, double z, EffectCloudEntity.Config config) {
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

   public static class Config implements INBTSerializable<CompoundNBT> {
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
         return "Config{name='" + this.name + '\'' + ", potion='" + this.potion + '\'' + ", effects=" + this.effects + '}';
      }

      public Config() {
         this("Dummy", Potions.field_185229_a, new ArrayList<>(), 600, 3.0F, -1, true, 1.0F);
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

      public static EffectCloudEntity.Config fromNBT(CompoundNBT nbt) {
         EffectCloudEntity.Config config = new EffectCloudEntity.Config();
         config.deserializeNBT(nbt);
         return config;
      }

      public String getName() {
         return this.name;
      }

      public Potion getPotion() {
         return Registry.field_212621_j.func_241873_b(new ResourceLocation(this.potion)).orElse(Potions.field_185229_a);
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

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("Name", this.name);
         nbt.func_74778_a("Potion", this.potion);
         ListNBT effectsList = new ListNBT();
         this.effects.forEach(cloudEffect -> effectsList.add(cloudEffect.serializeNBT()));
         nbt.func_218657_a("Effects", effectsList);
         nbt.func_74768_a("Duration", this.duration);
         nbt.func_74776_a("Radius", this.radius);
         nbt.func_74768_a("Color", this.color);
         nbt.func_74757_a("AffectsOwner", this.affectsOwner);
         nbt.func_74776_a("Chance", this.chance);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.name = nbt.func_74779_i("Name");
         this.potion = nbt.func_74779_i("Potion");
         ListNBT effectsList = nbt.func_150295_c("Effects", 10);
         this.effects = effectsList.stream().map(inbt -> EffectCloudEntity.Config.CloudEffect.fromNBT((CompoundNBT)inbt)).collect(Collectors.toList());
         this.duration = nbt.func_74762_e("Duration");
         this.radius = nbt.func_74760_g("Radius");
         this.color = nbt.func_74762_e("Color");
         this.affectsOwner = nbt.func_74767_n("AffectsOwner");
         this.chance = nbt.func_74760_g("Chance");
      }

      public static class CloudEffect implements INBTSerializable<CompoundNBT> {
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

         public CloudEffect(Effect effect, int duration, int amplifier, boolean showParticles, boolean showIcon) {
            this.effect = effect.getRegistryName().toString();
            this.duration = duration;
            this.amplifier = amplifier;
            this.showParticles = false;
            this.showIcon = true;
         }

         public static EffectCloudEntity.Config.CloudEffect fromNBT(CompoundNBT nbt) {
            EffectCloudEntity.Config.CloudEffect effect = new EffectCloudEntity.Config.CloudEffect();
            effect.deserializeNBT(nbt);
            return effect;
         }

         public Effect getEffect() {
            return (Effect)Registry.field_212631_t.func_82594_a(new ResourceLocation(this.effect));
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

         public EffectInstance create() {
            return new EffectInstance(this.getEffect(), this.getDuration(), this.getAmplifier(), false, this.showParticles(), this.showIcon());
         }

         public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.func_74778_a("Effect", this.effect);
            nbt.func_74768_a("Duration", this.duration);
            nbt.func_74768_a("Amplifier", this.amplifier);
            nbt.func_74757_a("ShowParticles", this.showParticles);
            nbt.func_74757_a("ShowIcon", this.showIcon);
            return nbt;
         }

         public void deserializeNBT(CompoundNBT nbt) {
            this.effect = nbt.func_74779_i("Effect");
            this.duration = nbt.func_74762_e("Duration");
            this.amplifier = nbt.func_74762_e("Amplifier");
            this.showParticles = nbt.func_74767_n("ShowParticles");
            this.showIcon = nbt.func_74767_n("ShowIcon");
         }
      }
   }
}
