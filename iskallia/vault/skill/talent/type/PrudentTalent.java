package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class PrudentTalent extends LearnableSkill {
   private float probability;

   public PrudentTalent(int unlockLevel, int learnPointCost, int regretPointCost, float probability) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.probability = probability;
   }

   public PrudentTalent() {
   }

   public float getProbability() {
      return this.probability;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.probability), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.probability = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.probability)).ifPresent(tag -> nbt.put("probability", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.probability = Adapters.FLOAT.readNbt(nbt.get("probability")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.probability)).ifPresent(element -> json.add("probability", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.probability = Adapters.FLOAT.readJson(json.get("probability")).orElseThrow();
   }
}
