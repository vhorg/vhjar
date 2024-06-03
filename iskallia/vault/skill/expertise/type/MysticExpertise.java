package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class MysticExpertise extends LearnableSkill {
   private int additionalCrystalVolume;

   public int getAdditionalCrystalVolume() {
      return this.additionalCrystalVolume;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.additionalCrystalVolume), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalCrystalVolume = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.additionalCrystalVolume)).ifPresent(tag -> nbt.put("additionalCrystalVolume", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalCrystalVolume = Adapters.INT.readNbt(nbt.get("additionalCrystalVolume")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.additionalCrystalVolume)).ifPresent(element -> json.add("additionalCrystalVolume", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalCrystalVolume = Adapters.INT.readJson(json.get("additionalCrystalVolume")).orElseThrow();
   }
}
