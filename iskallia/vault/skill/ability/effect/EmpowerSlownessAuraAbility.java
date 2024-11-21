package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.EntropyPoisonModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.config.IntRangeConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractEmpowerAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class EmpowerSlownessAuraAbility extends AbstractEmpowerAbility {
   private static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER;
   private static final int SLOWNESS_TICKS_DURATION = 40;
   private float radius;
   private int slownessAmplifier;

   public EmpowerSlownessAuraAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      int durationTicks,
      float radius,
      int slownessAmplifier
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, durationTicks);
      this.radius = radius;
      this.slownessAmplifier = slownessAmplifier;
   }

   public EmpowerSlownessAuraAbility() {
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, this, realRadius);
      }

      return realRadius;
   }

   public int getSlownessAmplifier() {
      return this.slownessAmplifier;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            int amplifier = Mth.clamp(this.getSlownessAmplifier() * 100, 0, 100);
            ModEffects.EMPOWER_SLOWNESS_AURA.addTo(player, amplifier);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.EMPOWER_SLOWNESS_AURA);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.EMPOWER_ICE_ARMOUR, SoundSource.MASTER, 0.7F, 1.0F);
            player.playNotifySound(ModSounds.EMPOWER_ICE_ARMOUR, SoundSource.MASTER, 0.7F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.EMPOWER_SLOWNESS_AURA)) {
            player.removeEffect(ModEffects.EMPOWER_SLOWNESS_AURA);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.EMPOWER_SLOWNESS_AURA));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.EMPOWER_SLOWNESS_AURA));
   }

   @SubscribeEvent
   public static void on(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player && player.hasEffect(ModEffects.EMPOWER_SLOWNESS_AURA)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         int poisonAmplifier = -1;

         for (ConfiguredModification<EntropyPoisonModification, IntRangeConfig, IntValue> mod : SpecialAbilityModification.getModifications(
            player, EntropyPoisonModification.class
         )) {
            poisonAmplifier += mod.value().getValue();
         }

         int poisonAmplifierSum = poisonAmplifier;

         for (EmpowerSlownessAuraAbility ability : abilities.getAll(EmpowerSlownessAuraAbility.class, Skill::isUnlocked)) {
            float radius = ability.getRadius(player);
            List<Mob> nearbyMobs = player.level
               .getNearbyEntities(
                  Mob.class, TargetingConditions.forCombat().range(radius).selector(MONSTER_PREDICATE), player, AABBHelper.create(player.position(), radius)
               );
            nearbyMobs.forEach(
               mob -> {
                  if (!mob.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
                     || mob.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() < ability.getSlownessAmplifier()) {
                     mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, ability.getSlownessAmplifier(), false, true, true));
                  }

                  if (poisonAmplifierSum >= 0 && (!mob.hasEffect(ModEffects.POISON_OVERRIDE) || mob.getEffect(ModEffects.POISON_OVERRIDE).getDuration() <= 7)) {
                     mob.addEffect(new MobEffectInstance(ModEffects.POISON_OVERRIDE, 40, poisonAmplifierSum, false, true, true));
                  }
               }
            );
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.slownessAmplifier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.slownessAmplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.slownessAmplifier)).ifPresent(tag -> nbt.put("slownessAmplifier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
      this.slownessAmplifier = Adapters.INT.readNbt(nbt.get("slownessAmplifier")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.INT.writeJson(Integer.valueOf(this.slownessAmplifier)).ifPresent(element -> json.add("slownessAmplifier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
      this.slownessAmplifier = Adapters.INT.readJson(json.get("slownessAmplifier")).orElse(0);
   }

   public static class EmpowerSlownessAuraEffect extends EmpowerAbility.EmpowerEffect {
      public EmpowerSlownessAuraEffect(int color, ResourceLocation resourceLocation) {
         super(EmpowerSlownessAuraAbility.EmpowerSlownessAuraEffect.class, color, resourceLocation);
      }
   }
}
