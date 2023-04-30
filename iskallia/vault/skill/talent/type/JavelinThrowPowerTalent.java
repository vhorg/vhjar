package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class JavelinThrowPowerTalent extends LearnableSkill {
   private float throwPower;

   public JavelinThrowPowerTalent(int unlockLevel, int learnPointCost, int regretPointCost, float throwPower) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.throwPower = throwPower;
   }

   public JavelinThrowPowerTalent() {
   }

   public float getThrowPower() {
      return this.throwPower;
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
      this.throwPower = Adapters.FLOAT.readNbt(nbt.get("throwPower")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.throwPower)).ifPresent(element -> json.add("throwPower", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.throwPower = Adapters.FLOAT.readJson(json.get("throwPower")).orElseThrow();
   }
}
