package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class JewelExpertise extends LearnableSkill {
   private int numberOfFreeCuts;

   public int getNumberOfFreeCuts() {
      return this.numberOfFreeCuts;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.numberOfFreeCuts), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.numberOfFreeCuts = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.numberOfFreeCuts)).ifPresent(tag -> nbt.put("numberOfFreeCuts", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.numberOfFreeCuts = Adapters.INT.readNbt(nbt.get("numberOfFreeCuts")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.numberOfFreeCuts)).ifPresent(element -> json.add("numberOfFreeCuts", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.numberOfFreeCuts = Adapters.INT.readJson(json.get("numberOfFreeCuts")).orElse(0);
   }
}
