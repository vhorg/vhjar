package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.EffectDurationHelper;
import iskallia.vault.util.damage.DamageOverTimeHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;

public class NovaDotAbility extends AbstractNovaAbility {
   private static final int PARTICLE_COLOR = TextColor.parseColor("#5e8a37").getValue();
   private int durationSeconds;

   public NovaDotAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      float percentAbilityPowerDealt,
      float knockbackStrengthMultiplier,
      int durationSeconds
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, percentAbilityPowerDealt, knockbackStrengthMultiplier);
      this.durationSeconds = durationSeconds;
   }

   public NovaDotAbility() {
   }

   public int getDurationSecondsUnmodified() {
      return this.durationSeconds;
   }

   public int getDurationTicks(LivingEntity entity) {
      int durationTicks = this.getDurationSecondsUnmodified() * 20;
      return EffectDurationHelper.adjustEffectDurationFloor(entity, durationTicks);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         List<LivingEntity> targetEntities = this.getTargetEntities(player.level, player, pos);
         float attackDamage = this.getAbilityPower(player);

         for (LivingEntity targetEntity : targetEntities) {
            DamageOverTimeHelper.invalidateAll(targetEntity);
            DamageOverTimeHelper.applyDamageOverTime(targetEntity, DamageSource.playerAttack(player), attackDamage, this.getDurationTicks(player));
            targetEntity.addEffect(new MobEffectInstance(ModEffects.NOVA_DOT, this.getDurationTicks(player), 0, false, true, false));
         }

         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, pos.x, pos.y, pos.z);
         areaEffectCloud.setOwner(player);
         areaEffectCloud.setRadius(this.getRadius(player));
         areaEffectCloud.setRadiusOnUse(-0.5F);
         areaEffectCloud.setWaitTime(0);
         areaEffectCloud.setDuration(4);
         areaEffectCloud.setPotion(Potions.EMPTY);
         areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
         areaEffectCloud.setFixedColor(PARTICLE_COLOR);
         areaEffectCloud.setParticle((ParticleOptions)ModParticles.NOVA_DOT.get());
         player.level.addFreshEntity(areaEffectCloud);
      });
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         player.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.2F, 1.0F);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationSeconds), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationSeconds = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.durationSeconds)).ifPresent(tag -> nbt.put("durationSeconds", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationSeconds = Adapters.INT.readNbt(nbt.get("durationSeconds")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.durationSeconds)).ifPresent(element -> json.add("durationSeconds", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationSeconds = Adapters.INT.readJson(json.get("durationSeconds")).orElse(0);
   }

   public static class NovaDotEffect extends MobEffect {
      public NovaDotEffect(int color, ResourceLocation id) {
         super(MobEffectCategory.HARMFUL, color);
         this.setRegistryName(id);
      }
   }
}
