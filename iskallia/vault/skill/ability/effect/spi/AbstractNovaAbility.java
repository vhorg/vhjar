package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.NovaRadiusModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AbilityPowerHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractNovaAbility extends InstantManaAbility {
   private float radius;
   private float percentAbilityPowerDealt;
   private float knockbackStrengthMultiplier;

   public AbstractNovaAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      float percentAbilityPowerDealt,
      float knockbackStrengthMultiplier
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.radius = radius;
      this.percentAbilityPowerDealt = percentAbilityPowerDealt;
      this.knockbackStrengthMultiplier = knockbackStrengthMultiplier;
   }

   protected AbstractNovaAbility() {
   }

   @Override
   public String getAbilityGroupName() {
      return "Nova";
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedRadius();
      if (attacker instanceof Player player) {
         for (ConfiguredModification<FloatValueConfig, NovaRadiusModification> mod : SpecialAbilityModification.getModifications(
            player, NovaRadiusModification.class
         )) {
            realRadius = mod.modification().adjustRadius(mod.config(), realRadius);
         }
      }

      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, realRadius);
      }

      return realRadius;
   }

   public float getPercentAbilityPowerDealt() {
      return this.percentAbilityPowerDealt;
   }

   public float getKnockbackStrengthMultiplier() {
      return this.knockbackStrengthMultiplier;
   }

   @NotNull
   protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos) {
      float radius = this.getRadius(attacker);
      return world.getNearbyEntities(
         LivingEntity.class,
         TargetingConditions.forCombat().range(radius).selector(entity -> !(entity instanceof Player)),
         attacker,
         AABBHelper.create(pos, radius)
      );
   }

   protected float getAbilityPower(ServerPlayer player) {
      return AbilityPowerHelper.getAbilityPower(player) * this.getPercentAbilityPowerDealt();
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> {
               float radius = this.getRadius(player);
               int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 50.0, 50.0, 200.0);
               ((ServerLevel)player.level)
                  .sendParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), particleCount, radius * 0.5, 0.5, radius * 0.5, 0.0);
            }
         );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentAbilityPowerDealt), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.knockbackStrengthMultiplier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.percentAbilityPowerDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.knockbackStrengthMultiplier = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(tag -> nbt.put("percentAbilityPowerDealt", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.knockbackStrengthMultiplier)).ifPresent(tag -> nbt.put("knockbackStrengthMultiplier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readNbt(nbt.get("percentAbilityPowerDealt")).orElse(0.0F);
      this.knockbackStrengthMultiplier = Adapters.FLOAT.readNbt(nbt.get("knockbackStrengthMultiplier")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(element -> json.add("percentAbilityPowerDealt", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.knockbackStrengthMultiplier)).ifPresent(element -> json.add("knockbackStrengthMultiplier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readJson(json.get("percentAbilityPowerDealt")).orElse(0.0F);
      this.knockbackStrengthMultiplier = Adapters.FLOAT.readJson(json.get("knockbackStrengthMultiplier")).orElse(0.0F);
   }
}
