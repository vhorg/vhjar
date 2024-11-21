package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class JewelExpertise extends LearnableSkill {
   private int additionalIdentifiedJewels;

   public int getAdditionalIdentifiedJewels() {
      return this.additionalIdentifiedJewels;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.additionalIdentifiedJewels), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalIdentifiedJewels = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.additionalIdentifiedJewels)).ifPresent(tag -> nbt.put("additionalIdentifiedJewels", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalIdentifiedJewels = Adapters.INT.readNbt(nbt.get("additionalIdentifiedJewels")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.additionalIdentifiedJewels)).ifPresent(element -> json.add("additionalIdentifiedJewels", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalIdentifiedJewels = Adapters.INT.readJson(json.get("additionalIdentifiedJewels")).orElse(0);
   }
}
