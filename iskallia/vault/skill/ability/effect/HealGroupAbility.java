package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractHealAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HealGroupAbility extends AbstractHealAbility {
   private float radius;

   public HealGroupAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float radius) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.radius = radius;
   }

   public HealGroupAbility() {
   }

   public float getRadius() {
      return this.radius;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               for (LivingEntity nearbyEntity : player.level
                  .getNearbyEntities(
                     LivingEntity.class,
                     TargetingConditions.forNonCombat().selector(entity -> entity instanceof Player).range(this.radius),
                     player,
                     AABBHelper.create(player.position(), this.radius)
                  )) {
                  nearbyEntity.heal(this.flatLifeHealed);
               }

               player.heal(this.flatLifeHealed);
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
                  Vec3 var4 = player.position();
                  serverLevel.sendParticles(
                     (SimpleParticleType)ModParticles.HEAL.get(), var4.x, var4.y, var4.z, 25, this.getRadius() * 0.5, 0.5, this.getRadius() * 0.5, 0.0
                  );
                  serverLevel.sendParticles(
                     new SphericalParticleOptions(
                        (ParticleType<SphericalParticleOptions>)ModParticles.HEAL_GROUP_EFFECT_RANGE.get(), this.getRadius(), new Vector3f(1.0F, 1.0F, 1.0F)
                     ),
                     var4.x,
                     var4.y,
                     var4.z,
                     200,
                     0.0,
                     0.0,
                     0.0,
                     0.0
                  );
                  serverLevel.sendParticles(
                     new SphericalParticleOptions(
                        (ParticleType<SphericalParticleOptions>)ModParticles.HEAL_GROUP_EFFECT_RING.get(), this.getRadius(), new Vector3f(1.0F, 1.0F, 1.0F)
                     ),
                     var4.x,
                     var4.y,
                     var4.z,
                     200,
                     0.0,
                     1.0,
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
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
         player.playNotifySound(ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
   }
}
