package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.VaultThrownJavelin;
import iskallia.vault.mana.FullManaPlayer;
import iskallia.vault.skill.ability.effect.spi.AbstractJavelinAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.talent.type.JavelinFrugalTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;

public class JavelinPiercingAbility extends AbstractJavelinAbility {
   private int piercing;

   public JavelinPiercingAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float percentAttackDamageDealt,
      float throwPower,
      int piercing
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, percentAttackDamageDealt, throwPower);
      this.piercing = piercing;
   }

   public JavelinPiercingAbility() {
   }

   public int getPiercing() {
      return this.piercing;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         VaultThrownJavelin thrownJavelin = new VaultThrownJavelin(player.level, player);
         thrownJavelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.getThrowPower(player), 1.0F);
         thrownJavelin.pickup = Pickup.DISALLOWED;
         thrownJavelin.setType("piercing");
         TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);

         for (JavelinFrugalTalent talent : talents.getAll(JavelinFrugalTalent.class, Skill::isUnlocked)) {
            if (talent.getFrugalChance() >= player.getRandom().nextFloat()) {
               context.getSource().setMana(FullManaPlayer.INSTANCE);
               thrownJavelin.setIsGhost();
            }
         }

         player.level.addFreshEntity(thrownJavelin);
         player.level.playSound((Player)null, thrownJavelin, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.piercing), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.piercing = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.piercing)).ifPresent(tag -> nbt.put("piercing", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.piercing = Adapters.INT.readNbt(nbt.get("piercing")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.piercing)).ifPresent(element -> json.add("piercing", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.piercing = Adapters.INT.readJson(json.get("piercing")).orElse(0);
   }
}
