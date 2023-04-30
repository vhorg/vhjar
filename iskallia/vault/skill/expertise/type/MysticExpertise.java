package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class MysticExpertise extends LearnableSkill {
   private float instabilityChanceReduction;

   public float getInstabilityChanceReduction() {
      return this.instabilityChanceReduction;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.instabilityChanceReduction), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.instabilityChanceReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.instabilityChanceReduction)).ifPresent(tag -> nbt.put("instabilityChanceReduction", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.instabilityChanceReduction = Adapters.FLOAT.readNbt(nbt.get("instabilityChanceReduction")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.instabilityChanceReduction)).ifPresent(element -> json.add("instabilityChanceReduction", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.instabilityChanceReduction = Adapters.FLOAT.readJson(json.get("instabilityChanceReduction")).orElseThrow();
   }
}
