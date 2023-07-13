package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.ability.effect.spi.core.ToggleManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.calc.AbilityPowerHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSmiteAbility extends ToggleManaAbility {
   private float additionalManaPerBolt;
   private float radius;
   private int intervalTicks;
   private int color;
   private float percentAbilityPowerDealt;
   private static final String TAG_ABILITY_DATA = "the_vault:ability/_Smite";
   private static final String TAG_REMAINING_INTERVAL_TICKS = "remainingIntervalTicks";
   public static final Predicate<Entity> ENTITY_PREDICATE = entity -> !(entity instanceof Player)
      && entity instanceof LivingEntity livingEntity
      && livingEntity.isAlive();

   public AbstractSmiteAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float radius,
      int intervalTicks,
      float percentAbilityPowerDealt,
      int color,
      float additionalManaPerBolt
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond);
      this.radius = radius;
      this.intervalTicks = intervalTicks;
      this.percentAbilityPowerDealt = percentAbilityPowerDealt;
      this.color = color;
      this.additionalManaPerBolt = additionalManaPerBolt;
   }

   public float getAdditionalManaPerBolt() {
      return this.additionalManaPerBolt;
   }

   protected AbstractSmiteAbility() {
   }

   public ActiveFlags getFlag() {
      return ActiveFlags.IS_SMITE_ATTACKING;
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, realRadius);
      }

      return realRadius;
   }

   public int getIntervalTicks() {
      return this.intervalTicks;
   }

   public float getAbilityPowerPercent() {
      return this.percentAbilityPowerDealt;
   }

   public int getColor() {
      return this.color;
   }

   public ToggleAbilityEffect getEffect() {
      return ModEffects.SMITE;
   }

   @Override
   public String getAbilityGroupName() {
      return "Smite";
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            this.getEffect().addTo(player, 0);
            clearRemainingInterval(player);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(this.getEffect());
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SMITE, SoundSource.PLAYERS, 0.5F, 1.0F);
            player.playNotifySound(ModSounds.SMITE, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(this.getEffect())) {
            player.removeEffect(this.getEffect());
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(this.getEffect()));
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(this.getEffect()));
   }

   private static CompoundTag getAbilityData(ServerPlayer serverPlayer) {
      CompoundTag persistentData = serverPlayer.getPersistentData();
      CompoundTag abilityData = persistentData.getCompound("the_vault:ability/_Smite");
      persistentData.put("the_vault:ability/_Smite", abilityData);
      return abilityData;
   }

   private static boolean decrementRemainingInterval(ServerPlayer serverPlayer, int intervalTicks) {
      boolean result = false;
      CompoundTag abilityData = getAbilityData(serverPlayer);
      int value = abilityData.getInt("remainingIntervalTicks") - 1;
      if (value <= 0) {
         result = true;
         value = intervalTicks;
      }

      abilityData.putInt("remainingIntervalTicks", value);
      return result;
   }

   private static void setPercentRemainingInterval(ServerPlayer serverPlayer, int intervalTicks) {
      CompoundTag abilityData = getAbilityData(serverPlayer);
      abilityData.putInt("remainingIntervalTicks", Math.min(intervalTicks, 10));
   }

   private static void clearRemainingInterval(ServerPlayer serverPlayer) {
      getAbilityData(serverPlayer).putInt("remainingIntervalTicks", 0);
   }

   @Override
   public Ability.TickResult doActiveTick(SkillContext context) {
      Ability.TickResult result = super.doActiveTick(context);
      return result == Ability.TickResult.PASS
         ? context.getSource()
            .as(ServerPlayer.class)
            .map(
               player -> {
                  if (!player.hasEffect(this.getEffect())) {
                     return Ability.TickResult.PASS;
                  } else if (decrementRemainingInterval(player, this.getIntervalTicks())
                     && this.doDamage(player, this.getRadius(player), this.getAbilityPowerPercent())) {
                     this.setActive(false);
                     this.doManaDepleted(context);
                     return Ability.TickResult.COOLDOWN;
                  } else {
                     return Ability.TickResult.PASS;
                  }
               }
            )
            .orElse(Ability.TickResult.PASS)
         : result;
   }

   private boolean doDamage(ServerPlayer player, float radius, float percentAbilityPowerDealt) {
      ArrayList<LivingEntity> result = new ArrayList<>();
      EntityHelper.getEntitiesInRange(player.level, player.position(), radius, ENTITY_PREDICATE, result);
      boolean applyCooldown = false;
      result.removeIf(entity -> entity instanceof EternalEntity);
      if (result.isEmpty()) {
         setPercentRemainingInterval(player, this.getIntervalTicks());
         return false;
      } else {
         LivingEntity livingEntity = result.get(player.level.random.nextInt(result.size()));
         AbstractSmiteAbility.SmiteBolt smiteBolt = (AbstractSmiteAbility.SmiteBolt)ModEntities.SMITE_ABILITY_BOLT.create(player.level);
         if (smiteBolt != null) {
            smiteBolt.setColor(this.getColor());
            smiteBolt.moveTo(livingEntity.position());
            player.level.addFreshEntity(smiteBolt);
         }

         if (!player.isCreative() && Mana.decrease(player, this.getAdditionalManaPerBolt()) <= 0.0F) {
            player.removeEffect(this.getEffect());
            applyCooldown = true;
         }

         ActiveFlags.IS_AP_ATTACKING.runIfNotSet(() -> this.getFlag().runIfNotSet(() -> {
            double damage = AbilityPowerHelper.getAbilityPower(player) * percentAbilityPowerDealt;
            livingEntity.hurt(DamageSource.playerAttack(player), (float)damage);
         }));
         if (this.getFlag() == ActiveFlags.IS_SMITE_BASE_ATTACKING) {
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
         } else {
            player.level
               .playSound(
                  null,
                  livingEntity.getX(),
                  livingEntity.getY(),
                  livingEntity.getZ(),
                  ModSounds.SMITE_BOLT,
                  SoundSource.PLAYERS,
                  0.7F,
                  1.5F + Mth.randomBetween(livingEntity.getRandom(), -0.2F, 0.2F)
               );
         }

         return applyCooldown;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.intervalTicks), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentAbilityPowerDealt), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalManaPerBolt), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.color), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.intervalTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.percentAbilityPowerDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.additionalManaPerBolt = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.color = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.intervalTicks)).ifPresent(tag -> nbt.put("intervalTicks", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(tag -> nbt.put("percentAbilityPowerDealt", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalManaPerBolt)).ifPresent(tag -> nbt.put("additionalManaPerBolt", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(tag -> nbt.put("color", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
      this.intervalTicks = Adapters.INT.readNbt(nbt.get("intervalTicks")).orElse(0);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readNbt(nbt.get("percentAbilityPowerDealt")).orElse(0.0F);
      this.additionalManaPerBolt = Adapters.FLOAT.readNbt(nbt.get("additionalManaPerBolt")).orElse(0.0F);
      this.intervalTicks = Adapters.INT.readNbt(nbt.get("color")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.INT.writeJson(Integer.valueOf(this.intervalTicks)).ifPresent(element -> json.add("intervalTicks", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(element -> json.add("percentAbilityPowerDealt", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.additionalManaPerBolt)).ifPresent(element -> json.add("additionalManaPerBolt", element));
         Adapters.INT.writeJson(Integer.valueOf(this.color)).ifPresent(element -> json.add("color", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
      this.intervalTicks = Adapters.INT.readJson(json.get("intervalTicks")).orElse(0);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readJson(json.get("percentAbilityPowerDealt")).orElse(0.0F);
      this.additionalManaPerBolt = Adapters.FLOAT.readJson(json.get("additionalManaPerBolt")).orElse(0.0F);
      this.color = Adapters.INT.readJson(json.get("color")).orElse(0);
   }

   public static class SmiteBolt extends Entity {
      private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(AbstractSmiteAbility.SmiteBolt.class, EntityDataSerializers.INT);
      private final boolean flashSky;
      private int life;
      public long seed;
      private int flashes;

      public SmiteBolt(EntityType<? extends AbstractSmiteAbility.SmiteBolt> entityType, Level level, boolean flashSky, int color) {
         super(entityType, level);
         this.flashSky = flashSky;
         this.noCulling = true;
         this.life = 2;
         this.seed = this.random.nextLong();
         this.flashes = this.random.nextInt(3) + 1;
         this.setColor(color);
      }

      @Nonnull
      public SoundSource getSoundSource() {
         return SoundSource.PLAYERS;
      }

      protected void defineSynchedData() {
         this.entityData.define(COLOR, 0);
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
   public static class SmiteBoltRenderer extends EntityRenderer<AbstractSmiteAbility.SmiteBolt> {
      public SmiteBoltRenderer(Context context) {
         super(context);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull AbstractSmiteAbility.SmiteBolt entity) {
         return TextureAtlas.LOCATION_BLOCKS;
      }

      @ParametersAreNonnullByDefault
      public void render(
         AbstractSmiteAbility.SmiteBolt entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight
      ) {
         float[] afloat = new float[8];
         float[] afloat1 = new float[8];
         float f = 0.0F;
         float f1 = 0.0F;
         Random random = new Random(entity.seed);

         for (int i = 7; i >= 0; i--) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += random.nextInt(11) - 5;
            f1 += random.nextInt(11) - 5;
         }

         VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lightning());
         matrixStack.pushPose();
         matrixStack.scale(0.1F, 0.1F, 0.1F);
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

                  int color = (Integer)entity.getEntityData().get(AbstractSmiteAbility.SmiteBolt.COLOR);
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

   public static class SmiteEffect extends ToggleAbilityEffect {
      public SmiteEffect(int color, ResourceLocation resourceLocation) {
         super(AbstractSmiteAbility.class, color, resourceLocation);
      }
   }
}
