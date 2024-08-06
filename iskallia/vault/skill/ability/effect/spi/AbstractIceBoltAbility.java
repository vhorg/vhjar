package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractIceBoltAbility extends InstantManaAbility {
   private float throwPower;

   public AbstractIceBoltAbility() {
   }

   public AbstractIceBoltAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float throwPower) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.throwPower = throwPower;
   }

   public float getThrowPower() {
      return this.throwPower;
   }

   @Override
   public String getAbilityGroupName() {
      return "Ice_Bolt";
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.throwPower), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.throwPower = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.throwPower)).ifPresent(tag -> nbt.put("throwPower", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.throwPower = Adapters.FLOAT.readNbt(nbt.get("throwPower")).orElse(1.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.throwPower)).ifPresent(tag -> json.add("throwPower", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.throwPower = Adapters.FLOAT.readJson(json.get("throwPower")).orElse(1.0F);
   }
}
