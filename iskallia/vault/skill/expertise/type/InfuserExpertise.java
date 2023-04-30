package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class InfuserExpertise extends LearnableSkill {
   private float negativeModifierRemovalChance;

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.negativeModifierRemovalChance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.negativeModifierRemovalChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.negativeModifierRemovalChance)).ifPresent(tag -> nbt.put("negativeModifierRemovalChance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.negativeModifierRemovalChance = Adapters.FLOAT.readNbt(nbt.get("negativeModifierRemovalChance")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.negativeModifierRemovalChance)).ifPresent(element -> json.add("negativeModifierRemovalChance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.negativeModifierRemovalChance = Adapters.FLOAT.readJson(json.get("negativeModifierRemovalChance")).orElseThrow();
   }

   public float getNegativeModifierRemovalChance() {
      return this.negativeModifierRemovalChance;
   }
}
