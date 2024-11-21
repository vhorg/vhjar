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
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.EffectDurationHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;

public class JavelinSightAbility extends AbstractJavelinAbility {
   private float radius;
   private int effectDuration;

   public JavelinSightAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float percentAttackDamageDealt,
      float throwPower,
      float radius,
      int effectDuration
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, percentAttackDamageDealt, throwPower);
      this.radius = radius;
      this.effectDuration = effectDuration;
   }

   public JavelinSightAbility() {
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, this, realRadius);
      }

      return realRadius;
   }

   public int getEffectDurationUnmodified() {
      return this.effectDuration;
   }

   public int getEffectDuration(LivingEntity entity) {
      int duration = this.getEffectDurationUnmodified();
      return EffectDurationHelper.adjustEffectDurationFloor(entity, duration);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         VaultThrownJavelin thrownJavelin = new VaultThrownJavelin(player.level, player);
         thrownJavelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.getThrowPower(player), 1.0F);
         thrownJavelin.pickup = Pickup.DISALLOWED;
         thrownJavelin.setType(VaultThrownJavelin.JavelinType.SIGHT);
         TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);

         for (JavelinFrugalTalent talent : talents.getAll(JavelinFrugalTalent.class, Skill::isUnlocked)) {
            if (talent.getFrugalChance() >= player.getRandom().nextFloat()) {
               context.getSource().setMana(FullManaPlayer.INSTANCE);
               thrownJavelin.setIsGhost();
            }
         }

         player.level.addFreshEntity(thrownJavelin);
         player.level.playSound(null, thrownJavelin, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.effectDuration), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.effectDuration = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.effectDuration)).ifPresent(tag -> nbt.put("effectDuration", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(10.0F);
      this.effectDuration = Adapters.INT.readNbt(nbt.get("effectDuration")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.INT.writeJson(Integer.valueOf(this.effectDuration)).ifPresent(element -> json.add("effectDuration", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(10.0F);
      this.effectDuration = Adapters.INT.readJson(json.get("effectDuration")).orElse(0);
   }
}
