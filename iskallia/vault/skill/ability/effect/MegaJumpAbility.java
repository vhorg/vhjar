package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.MegaJumpVelocityModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractMegaJumpAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class MegaJumpAbility extends AbstractMegaJumpAbility {
   private int height;

   public MegaJumpAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int height) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.height = height;
   }

   public MegaJumpAbility() {
   }

   public int getHeight() {
      return this.height;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               int height = this.getHeight();

               for (ConfiguredModification<IntValueConfig, MegaJumpVelocityModification> mod : SpecialAbilityModification.getModifications(
                  player, MegaJumpVelocityModification.class
               )) {
                  height = mod.modification().adjustHeightConfig(mod.config(), height);
               }

               if (height == 0) {
                  return Ability.ActionResult.successCooldownImmediate();
               } else {
                  double magnitude = height * 0.15;
                  double addY = -Math.min(0.0, player.getDeltaMovement().y());
                  player.push(0.0, addY + magnitude, 0.0);
                  player.startFallFlying();
                  player.hurtMarked = true;
                  return Ability.ActionResult.successCooldownImmediate();
               }
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> ((ServerLevel)player.level).sendParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0)
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.playNotifySound(ModSounds.MEGA_JUMP_SFX, SoundSource.PLAYERS, 0.3F, 1.0F);
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.MEGA_JUMP_SFX, SoundSource.PLAYERS, 0.3F, 1.0F);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.height), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.height = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.height)).ifPresent(tag -> nbt.put("height", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.height = Adapters.INT.readNbt(nbt.get("height")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.height)).ifPresent(element -> json.add("height", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.height = Adapters.INT.readJson(json.get("height")).orElse(0);
   }
}
