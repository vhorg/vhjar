package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.VaultStormArrow;
import iskallia.vault.skill.ability.effect.spi.AbstractStormArrowAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;

public class StormArrowBlizzardAbility extends AbstractStormArrowAbility {
   private int slowDuration;
   private int frostbiteDuration;
   private int amplifier;
   private int intervalHypothermiaTicks;
   private float frostbiteChance;

   public StormArrowBlizzardAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float percentAttackDamageDealt,
      float radius,
      int duration,
      int intervalTicks,
      int slowDuration,
      int frostbiteDuration,
      int amplifier,
      int intervalHypothermiaTicks,
      float frostbiteChance
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, percentAttackDamageDealt, radius, duration, intervalTicks);
      this.slowDuration = slowDuration;
      this.frostbiteDuration = frostbiteDuration;
      this.amplifier = amplifier;
      this.intervalHypothermiaTicks = intervalHypothermiaTicks;
      this.frostbiteChance = frostbiteChance;
   }

   public StormArrowBlizzardAbility() {
   }

   public int getSlowDuration() {
      return this.slowDuration;
   }

   public int getFrostbiteDuration() {
      return this.frostbiteDuration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getIntervalHypothermiaTicks() {
      return this.intervalHypothermiaTicks;
   }

   public float getFrostbiteChance() {
      return this.frostbiteChance;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         VaultStormArrow arrow = new VaultStormArrow(player.level, player);
         arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 0.0F);
         arrow.pickup = Pickup.DISALLOWED;
         arrow.setType("blizzard");
         arrow.setDuration(this.getDuration());
         arrow.setRadius(this.getRadius(player));
         arrow.setIntervalTicks(this.getIntervalTicks());
         arrow.setAbilityPowerPercent(this.getPercentAbilityPowerDealt());
         arrow.setSlowDuration(this.getSlowDuration());
         arrow.setFrostbiteDuration(this.getFrostbiteDuration());
         arrow.setAmplifier(this.getAmplifier());
         arrow.setIntervalHypothermiaTicks(this.getIntervalHypothermiaTicks());
         arrow.setFrostbiteChance(this.getFrostbiteChance());
         player.level.addFreshEntity(arrow);
         player.level.playSound((Player)null, player, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.25F);
         return Ability.ActionResult.successCooldownDelayed(this.getDuration());
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.frostbiteChance), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.slowDuration), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.frostbiteDuration), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.amplifier), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.intervalHypothermiaTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.frostbiteChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.slowDuration = Adapters.INT.readBits(buffer).orElseThrow();
      this.frostbiteDuration = Adapters.INT.readBits(buffer).orElseThrow();
      this.amplifier = Adapters.INT.readBits(buffer).orElseThrow();
      this.intervalHypothermiaTicks = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.frostbiteChance)).ifPresent(tag -> nbt.put("frostbiteChance", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.slowDuration)).ifPresent(tag -> nbt.put("slowDuration", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.frostbiteDuration)).ifPresent(tag -> nbt.put("frostbiteDuration", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.intervalHypothermiaTicks)).ifPresent(tag -> nbt.put("intervalHypothermiaTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.frostbiteChance = Adapters.FLOAT.readNbt(nbt.get("frostbiteChance")).orElse(0.05F);
      this.slowDuration = Adapters.INT.readNbt(nbt.get("slowDuration")).orElse(200);
      this.frostbiteDuration = Adapters.INT.readNbt(nbt.get("frostbiteDuration")).orElse(30);
      this.amplifier = Adapters.INT.readNbt(nbt.get("amplifier")).orElse(20);
      this.intervalHypothermiaTicks = Adapters.INT.readNbt(nbt.get("intervalHypothermiaTicks")).orElse(60);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.frostbiteChance)).ifPresent(element -> json.add("frostbiteChance", element));
         Adapters.INT.writeJson(Integer.valueOf(this.slowDuration)).ifPresent(element -> json.add("slowDuration", element));
         Adapters.INT.writeJson(Integer.valueOf(this.frostbiteDuration)).ifPresent(element -> json.add("frostbiteDuration", element));
         Adapters.INT.writeJson(Integer.valueOf(this.amplifier)).ifPresent(element -> json.add("amplifier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.intervalHypothermiaTicks)).ifPresent(element -> json.add("intervalHypothermiaTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.frostbiteChance = Adapters.FLOAT.readJson(json.get("frostbiteChance")).orElse(0.05F);
      this.slowDuration = Adapters.INT.readJson(json.get("slowDuration")).orElse(200);
      this.frostbiteDuration = Adapters.INT.readJson(json.get("frostbiteDuration")).orElse(30);
      this.amplifier = Adapters.INT.readJson(json.get("amplifier")).orElse(20);
      this.intervalHypothermiaTicks = Adapters.INT.readJson(json.get("intervalHypothermiaTicks")).orElse(60);
   }
}
