package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class BlackMarketExpertise extends LearnableSkill {
   private int timeReductionInMinutes;

   public BlackMarketExpertise(int unlockLevel, int learnPointCost, int regretPointCost, int timeReductionInMinutes) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.timeReductionInMinutes = timeReductionInMinutes;
   }

   public BlackMarketExpertise() {
   }

   public int getTimeReductionInMinutes() {
      return this.timeReductionInMinutes;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.timeReductionInMinutes), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.timeReductionInMinutes = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.timeReductionInMinutes)).ifPresent(tag -> nbt.put("timeReductionInMinutes", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.timeReductionInMinutes = Adapters.INT.readNbt(nbt.get("timeReductionInMinutes")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.timeReductionInMinutes)).ifPresent(element -> json.add("timeReductionInMinutes", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.timeReductionInMinutes = Adapters.INT.readJson(json.get("timeReductionInMinutes")).orElseThrow();
   }
}
