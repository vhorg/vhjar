package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.IceBoltEntity;
import iskallia.vault.gear.attribute.ability.special.NovaRadiusModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.StonefallFrostParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractIceBoltAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class IceBoltChunkAbility extends AbstractIceBoltAbility {
   private float radius;
   private int durationTicks;
   private int amplifier;
   private float glacialChance;

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

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public float getGlacialChance() {
      return this.glacialChance;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               IceBoltEntity arrow = new IceBoltEntity(
                  player,
                  IceBoltEntity.Model.CHUNK,
                  result -> {
                     float radius = this.getRadius(player);
                     Vec3 pos = result.getLocation();
                     ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new StonefallFrostParticleMessage(pos, this.getRadius(player)));
                     player.level.playSound(null, pos.x, pos.y, pos.z, ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);

                     for (LivingEntity nearbyEntity : player.level
                        .getNearbyEntities(
                           LivingEntity.class,
                           TargetingConditions.forCombat().selector(entity -> !(entity instanceof Player)).range(1000.0),
                           player,
                           AABBHelper.create(pos, radius)
                        )) {
                        nearbyEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED, this.getDurationTicks(), this.getAmplifier(), false, false, false));
                        CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, nearbyEntity));
                        if (player.level.random.nextFloat() <= this.glacialChance) {
                           nearbyEntity.removeEffect(ModEffects.GLACIAL_SHATTER);
                           nearbyEntity.addEffect(new MobEffectInstance(ModEffects.GLACIAL_SHATTER, this.getDurationTicks(), this.getAmplifier()));
                           player.level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.25F, 0.65F);
                        }
                     }
                  }
               );
               arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.getThrowPower(), 0.0F);
               player.level.addFreshEntity(arrow);
               player.level.playSound(null, player, ModSounds.ICE_BOLT_CHUNK, SoundSource.PLAYERS, 1.0F, player.level.random.nextFloat() * 0.3F + 0.9F);
               return Ability.ActionResult.successCooldownImmediate();
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.amplifier), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.glacialChance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.amplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.glacialChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.INT_SEGMENTED_7.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         Adapters.INT_SEGMENTED_3.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.glacialChance)).ifPresent(tag -> nbt.put("glacialChance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(1.0F);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readNbt(nbt.get("durationTicks")).orElse(0);
      this.amplifier = Adapters.INT_SEGMENTED_3.readNbt(nbt.get("amplifier")).orElse(0);
      this.glacialChance = Adapters.FLOAT.readNbt(nbt.get("glacialChance")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(tag -> json.add("radius", tag));
         Adapters.INT_SEGMENTED_7.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(tag -> json.add("durationTicks", tag));
         Adapters.INT_SEGMENTED_3.writeJson(Integer.valueOf(this.amplifier)).ifPresent(tag -> json.add("amplifier", tag));
         Adapters.FLOAT.writeJson(Float.valueOf(this.glacialChance)).ifPresent(tag -> json.add("glacialChance", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(1.0F);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readJson(json.get("durationTicks")).orElse(0);
      this.amplifier = Adapters.INT_SEGMENTED_3.readJson(json.get("amplifier")).orElse(0);
      this.glacialChance = Adapters.FLOAT.readJson(json.get("glacialChance")).orElse(0.0F);
   }
}
