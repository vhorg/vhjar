package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class LuckyAltarExpertise extends LearnableSkill {
   @Expose
   private float luckyAltarChance;

   public LuckyAltarExpertise(int unlockLevel, int learnPointCost, int regretPointCost, float luckyAltarChance) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.luckyAltarChance = luckyAltarChance;
   }

   public LuckyAltarExpertise() {
   }

   public float getLuckyAltarChance() {
      return this.luckyAltarChance;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.luckyAltarChance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.luckyAltarChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.luckyAltarChance)).ifPresent(tag -> nbt.put("luckyAltarChance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.luckyAltarChance = Adapters.FLOAT.readNbt(nbt.get("luckyAltarChance")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.luckyAltarChance)).ifPresent(element -> json.add("luckyAltarChance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.luckyAltarChance = Adapters.FLOAT.readJson(json.get("luckyAltarChance")).orElseThrow();
   }
}
