package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.easteregg.GrasshopperNinja;
import iskallia.vault.gear.attribute.ability.special.DashVelocityModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.MathUtilities;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DashAbility extends InstantManaAbility {
   private int extraDistance;

   public DashAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int extraDistance) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.extraDistance = extraDistance;
   }

   public DashAbility() {
   }

   public int getExtraDistance() {
      return this.extraDistance;
   }

   @Override
   public String getAbilityGroupName() {
      return "Dash";
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(Player.class)
         .map(
            player -> {
               Vec3 lookVector = player.getLookAngle();
               float magnitude = (10 + this.extraDistance) * 0.15F;
               double extraPitch = 10.0;

               for (ConfiguredModification<FloatValueConfig, DashVelocityModification> mod : SpecialAbilityModification.getModifications(
                  player, DashVelocityModification.class
               )) {
                  magnitude = mod.modification().adjustVelocity(mod.config(), magnitude);
               }

               Vec3 dashVector = new Vec3(lookVector.x(), lookVector.y(), lookVector.z());
               float initialYaw = (float)MathUtilities.extractYaw(dashVector);
               dashVector = MathUtilities.rotateYaw(dashVector, initialYaw);
               double dashPitch = Math.toDegrees(MathUtilities.extractPitch(dashVector));
               if (dashPitch + extraPitch > 90.0) {
                  dashVector = new Vec3(0.0, 1.0, 0.0);
                  dashPitch = 90.0;
               } else {
                  dashVector = MathUtilities.rotateRoll(dashVector, (float)Math.toRadians(-extraPitch));
                  dashVector = MathUtilities.rotateYaw(dashVector, -initialYaw);
                  dashVector = dashVector.normalize();
               }

               double coeff = 1.6 - MathUtilities.map(Math.abs(dashPitch), 0.0, 90.0, 0.6, 1.0);
               dashVector = dashVector.scale(magnitude * coeff);
               player.push(dashVector.x(), dashVector.y(), dashVector.z());
               player.hurtMarked = true;
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
            player -> ((ServerLevel)player.level).sendParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0)
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (GrasshopperNinja.isGrasshopperShape(player)) {
            player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.GRASSHOPPER_BRRR, SoundSource.PLAYERS, 0.2F, 1.0F);
            player.playNotifySound(ModSounds.GRASSHOPPER_BRRR, SoundSource.PLAYERS, 0.2F, 1.0F);
            GrasshopperNinja.achieve(player);
         } else {
            player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.DASH_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
            player.playNotifySound(ModSounds.DASH_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
         }
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.extraDistance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.extraDistance = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.extraDistance)).ifPresent(tag -> nbt.put("extraDistance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.extraDistance = Adapters.INT.readNbt(nbt.get("extraDistance")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.extraDistance)).ifPresent(element -> json.add("extraDistance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.extraDistance = Adapters.INT.readJson(json.get("extraDistance")).orElse(0);
   }
}
