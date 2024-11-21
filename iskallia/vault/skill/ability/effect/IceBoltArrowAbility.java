package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.IceBoltEntity;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractIceBoltAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.EffectDurationHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class IceBoltArrowAbility extends AbstractIceBoltAbility {
   private int durationTicks;
   private int amplifier;

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

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         IceBoltEntity entity = new IceBoltEntity(player, IceBoltEntity.Model.ARROW, result -> {
            if (result instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity living) {
               living.addEffect(new MobEffectInstance(ModEffects.CHILLED, this.getDurationTicks(player), this.getAmplifier(), false, false, false));
               CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, living));
            }
         });
         entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.getThrowPower(), 0.0F);
         player.level.addFreshEntity(entity);
         player.level.playSound(null, player, ModSounds.ICE_BOLT_ARROW, SoundSource.PLAYERS, 0.3F, player.level.random.nextFloat() * 0.6F + 0.8F);
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.amplifier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.amplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT_SEGMENTED_7.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         Adapters.INT_SEGMENTED_3.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readNbt(nbt.get("durationTicks")).orElse(0);
      this.amplifier = Adapters.INT_SEGMENTED_3.readNbt(nbt.get("amplifier")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT_SEGMENTED_7.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(tag -> json.add("durationTicks", tag));
         Adapters.INT_SEGMENTED_3.writeJson(Integer.valueOf(this.amplifier)).ifPresent(tag -> json.add("amplifier", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readJson(json.get("durationTicks")).orElse(0);
      this.amplifier = Adapters.INT_SEGMENTED_3.readJson(json.get("amplifier")).orElse(0);
   }
}
