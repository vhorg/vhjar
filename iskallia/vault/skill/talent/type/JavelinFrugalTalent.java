package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class JavelinFrugalTalent extends LearnableSkill {
   private float frugalChance;

   public JavelinFrugalTalent(int unlockLevel, int learnPointCost, int regretPointCost, float frugalChance) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.frugalChance = frugalChance;
   }

   public JavelinFrugalTalent() {
   }

   public float getFrugalChance() {
      return this.frugalChance;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.frugalChance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.frugalChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.frugalChance)).ifPresent(tag -> nbt.put("frugalChance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.frugalChance = Adapters.FLOAT.readNbt(nbt.get("frugalChance")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.frugalChance)).ifPresent(element -> json.add("frugalChance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.frugalChance = Adapters.FLOAT.readJson(json.get("frugalChance")).orElseThrow();
   }
}
