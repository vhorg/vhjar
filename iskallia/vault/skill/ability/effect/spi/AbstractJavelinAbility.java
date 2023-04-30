package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.talent.type.JavelinDamageTalent;
import iskallia.vault.skill.talent.type.JavelinThrowPowerTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public abstract class AbstractJavelinAbility extends InstantManaAbility {
   private float percentAttackDamageDealt;
   private float throwPower;

   public AbstractJavelinAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float percentAttackDamageDealt, float throwPower
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.percentAttackDamageDealt = percentAttackDamageDealt;
      this.throwPower = throwPower;
   }

   protected AbstractJavelinAbility() {
   }

   @Override
   public String getAbilityGroupName() {
      return "Javelin";
   }

   public float getPercentAttackDamageDealt() {
      return this.percentAttackDamageDealt;
   }

   public float getThrowPower() {
      return this.throwPower;
   }

   public float getThrowPower(ServerPlayer sPlayer) {
      float additionalThrowPower = 0.0F;
      TalentTree talents = PlayerTalentsData.get(sPlayer.getLevel()).getTalents(sPlayer);

      for (JavelinThrowPowerTalent talent : talents.getAll(JavelinThrowPowerTalent.class, Skill::isUnlocked)) {
         additionalThrowPower += talent.getThrowPower();
      }

      return this.throwPower + additionalThrowPower;
   }

   public float getAttackDamage(ServerPlayer player) {
      float additionalPercentDamage = 0.0F;
      TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);

      for (JavelinDamageTalent talent : talents.getAll(JavelinDamageTalent.class, Skill::isUnlocked)) {
         additionalPercentDamage += talent.getIncreasedDamage();
      }

      return (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE) * (this.getPercentAttackDamageDealt() + additionalPercentDamage);
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentAttackDamageDealt), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.throwPower), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.percentAttackDamageDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.throwPower = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentAttackDamageDealt)).ifPresent(tag -> nbt.put("percentAttackDamageDealt", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.throwPower)).ifPresent(tag -> nbt.put("throwPower", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.percentAttackDamageDealt = Adapters.FLOAT.readNbt(nbt.get("percentAttackDamageDealt")).orElse(1.0F);
      this.throwPower = Adapters.FLOAT.readNbt(nbt.get("throwPower")).orElse(1.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentAttackDamageDealt)).ifPresent(element -> json.add("percentAttackDamageDealt", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.throwPower)).ifPresent(element -> json.add("throwPower", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.percentAttackDamageDealt = Adapters.FLOAT.readJson(json.get("percentAttackDamageDealt")).orElse(1.0F);
      this.throwPower = Adapters.FLOAT.readJson(json.get("throwPower")).orElse(1.0F);
   }
}
