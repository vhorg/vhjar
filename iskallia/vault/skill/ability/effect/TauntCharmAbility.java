package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.Targeting;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractTauntAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.world.entity.ai.goal.GoalSelectorStack;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class TauntCharmAbility extends AbstractTauntAbility {
   public static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER
      && entity instanceof Mob
      && !(entity instanceof VaultBoss)
      && !entity.hasEffect(ModEffects.TAUNT_CHARM)
      && !entity.hasEffect(ModEffects.TAUNT_REPEL_MOB);
   public static final Predicate<LivingEntity> LIVING_ENTITY_PREDICATE = entity -> !entity.hasEffect(ModEffects.TAUNT_CHARM) && !(entity instanceof Player);
   private int maxCharmedMobs;
   private float percentPlayerDamage;
   public static final String TAG_ABILITY_DATA = "the_vault:Taunt_Charm";
   public static final String TAG_PLAYER_UUID = "playerUUID";
   public static final String TAG_PERCENT_PLAYER_DAMAGE = "percentPlayerDamage";
   private static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("46489b0a-0cfd-4e51-87ab-d656b8a13db2");

   public TauntCharmAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      int durationTicks,
      int maxCharmedMobs,
      float percentPlayerDamage
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, durationTicks);
      this.maxCharmedMobs = maxCharmedMobs;
      this.percentPlayerDamage = percentPlayerDamage;
   }

   public TauntCharmAbility() {
   }

   public int getMaxCharmedMobs() {
      return this.maxCharmedMobs;
   }

   public float getPercentPlayerDamage() {
      return this.percentPlayerDamage;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               float radius = this.getRadius(player);
               List<Mob> nearbyMobs = player.level
                  .getNearbyEntities(
                     Mob.class, TargetingConditions.forCombat().range(radius).selector(MONSTER_PREDICATE), player, AABBHelper.create(player.position(), radius)
                  );
               Collections.shuffle(nearbyMobs);
               int maxCharmedMobs = this.getMaxCharmedMobs();

               for (Mob mob : nearbyMobs) {
                  if (maxCharmedMobs <= 0) {
                     break;
                  }

                  if (player.hasLineOfSight(mob)) {
                     mob.setTarget(null);
                     Brain<?> brain = mob.getBrain();
                     brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                     brain.eraseMemory(MemoryModuleType.ANGRY_AT);
                     brain.eraseMemory(MemoryModuleType.UNIVERSAL_ANGER);
                     mob.addEffect(new MobEffectInstance(ModEffects.TAUNT_CHARM, this.durationTicks, 0, false, true, false));
                     setAbilityData(mob, player.getUUID(), this.getPercentPlayerDamage());
                     applySpeedModifier(mob, 0.5F);
                     maxCharmedMobs--;
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
               if (player.level instanceof ServerLevel serverLevel) {
                  Vec3 position = player.position();
                  serverLevel.sendParticles(
                     new SphericalParticleOptions(
                        (ParticleType<SphericalParticleOptions>)ModParticles.TAUNT_CHARM_EFFECT_RANGE.get(),
                        this.getRadius(player),
                        new Vector3f(1.0F, 1.0F, 1.0F)
                     ),
                     position.x,
                     position.y,
                     position.z,
                     200,
                     0.0,
                     0.0,
                     0.0,
                     0.0
                  );
               }
            }
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         SoundEvent sound = ModSounds.TAUNT_CHARM;
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.playNotifySound(sound, SoundSource.PLAYERS, 1.0F, 1.0F);
      });
   }

   public static void setAbilityData(Mob mob, UUID playerUUID, float percentPlayerDamage) {
      CompoundTag persistentData = mob.getPersistentData();
      CompoundTag abilityData = new CompoundTag();
      abilityData.putUUID("playerUUID", playerUUID);
      abilityData.putFloat("percentPlayerDamage", percentPlayerDamage);
      persistentData.put("the_vault:Taunt_Charm", abilityData);
   }

   public static void removeAbilityData(Mob mob) {
      CompoundTag persistentData = mob.getPersistentData();
      persistentData.remove("the_vault:Taunt_Charm");
   }

   public static CompoundTag getAbilityData(Mob mob) {
      CompoundTag persistentData = mob.getPersistentData();
      if (!persistentData.contains("the_vault:Taunt_Charm", 10)) {
         return null;
      } else {
         CompoundTag abilityData = persistentData.getCompound("the_vault:Taunt_Charm");
         return abilityData.contains("playerUUID", 11) && abilityData.contains("percentPlayerDamage", 5) ? abilityData : null;
      }
   }

   private static void applySpeedModifier(Mob mob, float value) {
      AttributeInstance attributeInstance = mob.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attributeInstance != null) {
         attributeInstance.addTransientModifier(
            new AttributeModifier(MOVEMENT_SPEED_MODIFIER_UUID, "Charmed mob movement speed bonus", value, Operation.MULTIPLY_BASE)
         );
      }
   }

   private static void removeSpeedModifier(Mob mob) {
      AttributeInstance attributeInstance = mob.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attributeInstance != null) {
         attributeInstance.removeModifier(MOVEMENT_SPEED_MODIFIER_UUID);
      }
   }

   @SubscribeEvent
   public static void on(LivingAttackEvent event) {
      if (event.getEntity().getLevel() instanceof ServerLevel serverLevel) {
         if (event.getSource().getEntity() instanceof Mob mob) {
            CompoundTag abilityData = getAbilityData(mob);
            if (abilityData != null) {
               UUID playerUUID = abilityData.getUUID("playerUUID");
               Player player = serverLevel.getPlayerByUUID(playerUUID);
               if (player != null) {
                  AttributeInstance attributeInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
                  if (attributeInstance != null) {
                     LivingEntity targetEntity = event.getEntityLiving();
                     ActiveFlags.IS_CHARMED_ATTACKING.runIfNotSet(() -> {
                        float percentPlayerDamage = abilityData.getFloat("percentPlayerDamage");
                        float damage = (float)(attributeInstance.getValue() * percentPlayerDamage);
                        Vec3 movement = targetEntity.getDeltaMovement();
                        targetEntity.hurt(DamageSource.playerAttack(player), damage);
                        targetEntity.setDeltaMovement(new Vec3(movement.x, Math.min(movement.y + 0.2, 0.2), movement.z));
                     });
                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.maxCharmedMobs), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentPlayerDamage), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.maxCharmedMobs = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.percentPlayerDamage = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.maxCharmedMobs)).ifPresent(tag -> nbt.put("maxCharmedMobs", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentPlayerDamage)).ifPresent(tag -> nbt.put("percentPlayerDamage", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.maxCharmedMobs = Adapters.INT.readNbt(nbt.get("maxCharmedMobs")).orElse(0);
      this.percentPlayerDamage = Adapters.FLOAT.readNbt(nbt.get("percentPlayerDamage")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.maxCharmedMobs)).ifPresent(element -> json.add("maxCharmedMobs", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentPlayerDamage)).ifPresent(element -> json.add("percentPlayerDamage", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.maxCharmedMobs = Adapters.INT.readJson(json.get("maxCharmedMobs")).orElse(0);
      this.percentPlayerDamage = Adapters.FLOAT.readJson(json.get("percentPlayerDamage")).orElse(0.0F);
   }

   static {
      Targeting.addIgnoredTargetOverride(
         (attacker, target) -> attacker.hasEffect(ModEffects.TAUNT_CHARM)
            && (target instanceof Player || target instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.TAUNT_CHARM))
      );
      Targeting.addForcedTargetOverride(
         (attacker, target) -> attacker.hasEffect(ModEffects.TAUNT_CHARM)
            && target instanceof LivingEntity livingEntity
            && LIVING_ENTITY_PREDICATE.test(livingEntity)
      );
   }

   public static class CharmedAttackTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public CharmedAttackTargetGoal(Mob mob) {
         super(mob, LivingEntity.class, 10, true, false, TauntCharmAbility.LIVING_ENTITY_PREDICATE);
      }

      public void start() {
         super.start();
         this.targetMob = this.mob.getTarget();
      }
   }

   public static class TauntCharmMobEffect extends MobEffect {
      public TauntCharmMobEffect(int color, ResourceLocation id) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(id);
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof Mob mob) {
            if (mob.targetSelector instanceof GoalSelectorStack goalSelectorStack) {
               goalSelectorStack.pushGoalSet();
               mob.targetSelector.addGoal(0, new TauntCharmAbility.CharmedAttackTargetGoal(mob));
            }
         }
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof Mob mob) {
            if (mob.targetSelector instanceof GoalSelectorStack goalSelectorStack) {
               goalSelectorStack.popGoalSet();
               TauntCharmAbility.removeAbilityData(mob);
               TauntCharmAbility.removeSpeedModifier(mob);
            }
         }
      }
   }
}
