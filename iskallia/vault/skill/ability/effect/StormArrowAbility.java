package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
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
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;

public class StormArrowAbility extends AbstractStormArrowAbility {
   public StormArrowAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float percentAttackDamageDealt,
      float radius,
      int duration,
      int intervalTicks
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, percentAttackDamageDealt, radius, duration, intervalTicks);
   }

   public StormArrowAbility() {
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         int duration = this.getDuration(player);
         VaultStormArrow arrow = new VaultStormArrow(player.level, player);
         arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 0.0F);
         arrow.pickup = Pickup.DISALLOWED;
         arrow.setStormArrowType(VaultStormArrow.StormType.BASE);
         arrow.setDuration(duration);
         arrow.setRadius(this.getRadius(player));
         arrow.setIntervalTicks(this.getIntervalTicks());
         arrow.setAbilityPowerPercent(this.getPercentAbilityPowerDealt());
         player.level.addFreshEntity(arrow);
         player.level.playSound(null, player, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.25F);
         return Ability.ActionResult.successCooldownDelayed(duration);
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> (CompoundTag)nbt);
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> (JsonObject)json);
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
   }
}
