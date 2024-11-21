package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantAbility;
import iskallia.vault.skill.base.SkillContext;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class ExecuteAbility extends InstantAbility {
   private float damageHealthPercentage;
   private int effectDurationTicks;

   public ExecuteAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float damageHealthPercentage, int effectDurationTicks) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks);
      this.damageHealthPercentage = damageHealthPercentage;
      this.effectDurationTicks = effectDurationTicks;
   }

   public ExecuteAbility() {
   }

   public float getDamageHealthPercentage() {
      return this.damageHealthPercentage;
   }

   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return Ability.ActionResult.fail();
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.EXECUTION_SFX, SoundSource.PLAYERS, 0.4F, 1.0F);
         player.playNotifySound(ModSounds.EXECUTION_SFX, SoundSource.PLAYERS, 0.4F, 1.0F);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageHealthPercentage), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.effectDurationTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageHealthPercentage = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.effectDurationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageHealthPercentage)).ifPresent(tag -> nbt.put("damageHealthPercentage", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.effectDurationTicks)).ifPresent(tag -> nbt.put("effectDurationTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageHealthPercentage = Adapters.FLOAT.readNbt(nbt.get("damageHealthPercentage")).orElse(0.0F);
      this.effectDurationTicks = Adapters.INT.readNbt(nbt.get("effectDurationTicks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageHealthPercentage)).ifPresent(element -> json.add("damageHealthPercentage", element));
         Adapters.INT.writeJson(Integer.valueOf(this.effectDurationTicks)).ifPresent(element -> json.add("effectDurationTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageHealthPercentage = Adapters.FLOAT.readJson(json.get("damageHealthPercentage")).orElse(0.0F);
      this.effectDurationTicks = Adapters.INT.readJson(json.get("effectDurationTicks")).orElse(0);
   }
}
