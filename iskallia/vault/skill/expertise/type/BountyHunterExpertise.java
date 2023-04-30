package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class BountyHunterExpertise extends LearnableSkill {
   private int waitingPeriodReduction;
   private float abandonedPenaltyReduction;
   private int maxActive;

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.waitingPeriodReduction), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.abandonedPenaltyReduction), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.maxActive), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.waitingPeriodReduction = Adapters.INT.readBits(buffer).orElseThrow();
      this.abandonedPenaltyReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.maxActive = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.waitingPeriodReduction)).ifPresent(tag -> nbt.put("waitingPeriodReduction", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.abandonedPenaltyReduction)).ifPresent(tag -> nbt.put("abandonedPenaltyReduction", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxActive)).ifPresent(tag -> nbt.put("maxActive", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.waitingPeriodReduction = Adapters.INT.readNbt(nbt.get("waitingPeriodReduction")).orElseThrow();
      this.abandonedPenaltyReduction = Adapters.FLOAT.readNbt(nbt.get("abandonedPenaltyReduction")).orElseThrow();
      this.maxActive = Adapters.INT.readNbt(nbt.get("maxActive")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.waitingPeriodReduction)).ifPresent(element -> json.add("waitingPeriodReduction", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.abandonedPenaltyReduction)).ifPresent(element -> json.add("abandonedPenaltyReduction", element));
         Adapters.INT.writeJson(Integer.valueOf(this.maxActive)).ifPresent(element -> json.add("maxActive", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.waitingPeriodReduction = Adapters.INT.readJson(json.get("waitingPeriodReduction")).orElseThrow();
      this.abandonedPenaltyReduction = Adapters.FLOAT.readJson(json.get("abandonedPenaltyReduction")).orElseThrow();
      this.maxActive = Adapters.INT.readJson(json.get("maxActive")).orElseThrow();
   }

   public int getWaitingPeriodReduction() {
      return this.waitingPeriodReduction;
   }

   public float getAbandonedPenaltyReduction() {
      return this.abandonedPenaltyReduction;
   }

   public int getMaxActive() {
      return this.maxActive;
   }
}
