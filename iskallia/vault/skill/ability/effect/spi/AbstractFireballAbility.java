package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.AbilityPowerHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractFireballAbility extends InstantManaAbility {
   private float percentAbilityPowerDealt;
   private float radius;

   public AbstractFireballAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float percentAbilityPowerDealt, float radius
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.percentAbilityPowerDealt = percentAbilityPowerDealt;
      this.radius = radius;
   }

   protected AbstractFireballAbility() {
   }

   public float getPercentAbilityPowerDealt() {
      return this.percentAbilityPowerDealt;
   }

   public float getRadius() {
      return this.radius;
   }

   public float getAbilityPower(ServerPlayer player) {
      return AbilityPowerHelper.getAbilityPower(player) * this.getPercentAbilityPowerDealt();
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentAbilityPowerDealt), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(tag -> nbt.put("percentAbilityPowerDealt", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readNbt(nbt.get("percentAbilityPowerDealt")).orElse(1.0F);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(1.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentAbilityPowerDealt)).ifPresent(element -> json.add("percentAbilityPowerDealt", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.percentAbilityPowerDealt = Adapters.FLOAT.readJson(json.get("percentAbilityPowerDealt")).orElse(1.0F);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(1.0F);
   }
}
