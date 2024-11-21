package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.VaultFireball;
import iskallia.vault.skill.ability.effect.spi.AbstractFireballAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;

public class FireballFireshotAbility extends AbstractFireballAbility {
   public FireballFireshotAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float percentAttackDamageDealt, float radius
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, percentAttackDamageDealt, radius);
   }

   public FireballFireshotAbility() {
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         VaultFireball fireball = new VaultFireball(player.level, player);
         fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 0.0F);
         fireball.pickup = Pickup.DISALLOWED;
         fireball.setType(VaultFireball.FireballType.FIRESHOT);
         player.level.addFreshEntity(fireball);
         player.level.playSound(null, player, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.5F, 1.4F);
         return Ability.ActionResult.successCooldownImmediate();
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
