package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.effect.InfiniteDurationEffect;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.ability.special.FrostNovaVulnerabilityModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.config.IntRangeConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.EffectDurationHelper;
import iskallia.vault.util.effect.ScheduledEffectHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class NovaSpeedAbility extends AbstractNovaAbility {
   private int durationTicks;
   private int amplifier;
   private int damageIntervalTicks;
   private static final String TAG_ABILITY_DATA = "the_vault:ability/Nova_Slow";
   private static final String TAG_INTERVAL_TICKS = "intervalTicks";
   private static final String TAG_REMAINING_INTERVAL_TICKS = "remainingIntervalTicks";
   private static final String TAG_PLAYER_UUID = "playerUUID";

   public NovaSpeedAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      float percentAttackDamageDealt,
      float knockbackStrengthMultiplier,
      int durationTicks,
      int amplifier,
      int damageIntervalTicks
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, percentAttackDamageDealt, knockbackStrengthMultiplier);
      this.durationTicks = durationTicks;
      this.amplifier = amplifier;
      this.damageIntervalTicks = damageIntervalTicks;
   }

   public NovaSpeedAbility() {
   }

   public int getDurationTicksUnmodified() {
      return this.durationTicks;
   }

   public int getDurationTicks(LivingEntity entity) {
      int duration = this.getDurationTicksUnmodified();
      return EffectDurationHelper.adjustEffectDurationFloor(entity, duration);
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getDamageIntervalTicks() {
      return this.damageIntervalTicks;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               float radius = this.getRadius(player);
               Vec3 pos = context.getSource().getPos().orElse(player.position());
               List<LivingEntity> nearbyEntities = player.level
                  .getNearbyEntities(
                     LivingEntity.class,
                     TargetingConditions.forCombat().selector(entity -> !(entity instanceof Player)).range(radius),
                     player,
                     AABBHelper.create(pos, radius)
                  );
               int vulnerabilityLevel = 0;

               for (ConfiguredModification<FrostNovaVulnerabilityModification, IntRangeConfig, IntValue> mod : SpecialAbilityModification.getModifications(
                  player, FrostNovaVulnerabilityModification.class
               )) {
                  vulnerabilityLevel += mod.value().getValue();
               }

               for (LivingEntity nearbyEntity : nearbyEntities) {
                  nearbyEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED, this.getDurationTicks(player), this.getAmplifier(), false, false, false));
                  CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, nearbyEntity));
                  if (!nearbyEntity.hasEffect(ModEffects.HYPOTHERMIA)) {
                     ScheduledEffectHelper.invalidateAll(nearbyEntity, ModEffects.HYPOTHERMIA);
                     ScheduledEffectHelper.scheduleEffect(nearbyEntity, ModEffects.HYPOTHERMIA.timedInstance(0, 400, true), this.getDurationTicks(player));
                     setAbilityData(nearbyEntity, this.getDamageIntervalTicks(), player.getUUID());
                  }

                  if (vulnerabilityLevel > 0) {
                     int amplifier = vulnerabilityLevel - 1;
                     nearbyEntity.addEffect(new MobEffectInstance(ModEffects.VULNERABLE, this.getDurationTicks(player) * 4, amplifier, false, false));
                  }
               }

               return Ability.ActionResult.successCooldownImmediate();
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> {
               float radius = this.getRadius(player);
               Vec3 pos = context.getSource().getPos().orElse(player.position());
               int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
               ((ServerLevel)player.level)
                  .sendParticles((SimpleParticleType)ModParticles.NOVA_SPEED.get(), pos.x, pos.y, pos.z, particleCount, radius * 0.5, 0.25, radius * 0.5, 0.0);
            }
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         player.level.playSound(player, pos.x, pos.y, pos.z, ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
      });
   }

   private static void setAbilityData(LivingEntity livingEntity, int intervalTicks, UUID playerUUID) {
      CompoundTag abilityData = getAbilityData(livingEntity);
      abilityData.putInt("intervalTicks", intervalTicks);
      abilityData.putInt("remainingIntervalTicks", 0);
      abilityData.putUUID("playerUUID", playerUUID);
   }

   private static boolean isAbilityDataValid(CompoundTag data) {
      return data.contains("intervalTicks") && data.contains("remainingIntervalTicks") && data.contains("playerUUID");
   }

   private static CompoundTag getAbilityData(LivingEntity livingEntity) {
      CompoundTag persistentData = livingEntity.getPersistentData();
      CompoundTag abilityData = persistentData.getCompound("the_vault:ability/Nova_Slow");
      persistentData.put("the_vault:ability/Nova_Slow", abilityData);
      return abilityData;
   }

   private static boolean decrementRemainingInterval(LivingEntity livingEntity) {
      boolean result = false;
      CompoundTag abilityData = getAbilityData(livingEntity);
      int value = abilityData.getInt("remainingIntervalTicks") - 1;
      if (value <= 0) {
         result = true;
         value = abilityData.getInt("intervalTicks");
      }

      abilityData.putInt("remainingIntervalTicks", value);
      return result;
   }

   @SubscribeEvent
   public static void on(LivingUpdateEvent event) {
      LivingEntity livingEntity = event.getEntityLiving();
      if (livingEntity.getLevel() instanceof ServerLevel serverLevel && livingEntity.hasEffect(ModEffects.HYPOTHERMIA)) {
         if (decrementRemainingInterval(livingEntity)) {
            CompoundTag abilityData = getAbilityData(livingEntity);
            if (!isAbilityDataValid(abilityData)) {
               return;
            }

            UUID playerUUID = abilityData.getUUID("playerUUID");
            ServerPlayer serverPlayer = serverLevel.getServer().getPlayerList().getPlayer(playerUUID);
            if (serverPlayer == null) {
               return;
            }

            DamageSource srcPlayerAttack = DamageSource.playerAttack(serverPlayer);
            if (livingEntity.isInvulnerableTo(srcPlayerAttack)) {
               return;
            }

            ActiveFlags.IS_EFFECT_ATTACKING.runIfNotSet(() -> {
               Vec3 movement = livingEntity.getDeltaMovement();
               livingEntity.hurt(DamageSource.playerAttack(serverPlayer), 1.0F);
               livingEntity.setDeltaMovement(movement);
            });
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.amplifier), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.damageIntervalTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.amplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.damageIntervalTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.damageIntervalTicks)).ifPresent(tag -> nbt.put("damageIntervalTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
      this.amplifier = Adapters.INT.readNbt(nbt.get("amplifier")).orElse(0);
      this.damageIntervalTicks = Adapters.INT.readNbt(nbt.get("damageIntervalTicks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(element -> json.add("durationTicks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.amplifier)).ifPresent(element -> json.add("amplifier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.damageIntervalTicks)).ifPresent(element -> json.add("damageIntervalTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
      this.amplifier = Adapters.INT.readJson(json.get("amplifier")).orElse(0);
      this.damageIntervalTicks = Adapters.INT.readJson(json.get("damageIntervalTicks")).orElse(0);
   }

   public static class HypothermiaEffect extends InfiniteDurationEffect {
      private static final String MOVEMENT_SPEED_UUID = "186fd101-ac04-4e84-9e1d-03ef06776bf7";
      private static final float MOVEMENT_SPEED_AMOUNT = -0.15F;

      public HypothermiaEffect(int color, ResourceLocation id) {
         super(MobEffectCategory.HARMFUL, color, id);
         this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "186fd101-ac04-4e84-9e1d-03ef06776bf7", -0.15F, Operation.MULTIPLY_TOTAL);
      }
   }
}
