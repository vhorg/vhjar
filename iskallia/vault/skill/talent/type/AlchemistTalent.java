package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class AlchemistTalent extends LearnableSkill {
   private float durationIncrease;

   public AlchemistTalent(int unlockLevel, int learnPointCost, int regretPointCost, float durationIncrease) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.durationIncrease = durationIncrease;
   }

   public AlchemistTalent() {
   }

   public float getDurationIncrease() {
      return this.durationIncrease;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.durationIncrease), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationIncrease = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.durationIncrease)).ifPresent(tag -> nbt.put("durationIncrease", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationIncrease = Adapters.FLOAT.readNbt(nbt.get("durationIncrease")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.durationIncrease)).ifPresent(element -> json.add("durationIncrease", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationIncrease = Adapters.FLOAT.readJson(json.get("durationIncrease")).orElseThrow();
   }
}
