package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.AbilityPowerHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.EffectDurationHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractStormArrowAbility extends InstantManaAbility {
   private float percentAbilityPowerDealt;
   private float radius;
   private int cloudDuration;
   private int intervalTicks;

   public AbstractStormArrowAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float percentAbilityPowerDealt,
      float radius,
      int cloudDuration,
      int intervalTicks
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.percentAbilityPowerDealt = percentAbilityPowerDealt;
      this.radius = radius;
      this.cloudDuration = cloudDuration;
      this.intervalTicks = intervalTicks;
   }

   protected AbstractStormArrowAbility() {
   }

   public float getPercentAbilityPowerDealt() {
      return this.percentAbilityPowerDealt;
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

   public int getUnmodifiedDuration() {
      return this.cloudDuration;
   }

   public int getDuration(LivingEntity entity) {
      int duration = this.getUnmodifiedDuration();
      return EffectDurationHelper.adjustEffectDurationFloor(entity, duration);
   }

   public int getIntervalTicks() {
      return this.intervalTicks;
   }

   public float getAbilityPower(ServerPlayer player) {
      return AbilityPowerHelper.getAbilityPower(player) * this.getPercentAbilityPowerDealt();
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentAbilityPowerDealt), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.cloudDuration), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.intervalTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.cloudDuration = Adapters.INT.readBits(buffer).orElseThrow();
      this.intervalTicks = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(tag -> nbt.put("percentAbilityPowerDealt", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.cloudDuration)).ifPresent(tag -> nbt.put("cloudDuration", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.intervalTicks)).ifPresent(tag -> nbt.put("intervalTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readNbt(nbt.get("percentAbilityPowerDealt")).orElse(1.0F);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(1.0F);
      this.cloudDuration = Adapters.INT.readNbt(nbt.get("cloudDuration")).orElse(200);
      this.intervalTicks = Adapters.INT.readNbt(nbt.get("intervalTicks")).orElse(10);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(element -> json.add("percentAbilityPowerDealt", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.INT.writeJson(Integer.valueOf(this.cloudDuration)).ifPresent(element -> json.add("cloudDuration", element));
         Adapters.INT.writeJson(Integer.valueOf(this.intervalTicks)).ifPresent(element -> json.add("intervalTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readJson(json.get("percentAbilityPowerDealt")).orElse(1.0F);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(1.0F);
      this.cloudDuration = Adapters.INT.readJson(json.get("cloudDuration")).orElse(200);
      this.intervalTicks = Adapters.INT.readJson(json.get("intervalTicks")).orElse(10);
   }
}
