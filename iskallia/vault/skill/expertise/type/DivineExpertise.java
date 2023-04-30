package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class DivineExpertise extends LearnableSkill {
   private float affinityIncrease;

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.affinityIncrease), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.affinityIncrease = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.affinityIncrease)).ifPresent(tag -> nbt.put("affinityIncrease", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.affinityIncrease = Adapters.FLOAT.readNbt(nbt.get("affinityIncrease")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.affinityIncrease)).ifPresent(element -> json.add("affinityIncrease", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.affinityIncrease = Adapters.FLOAT.readJson(json.get("affinityIncrease")).orElseThrow();
   }

   public float getAffinityIncrease() {
      return this.affinityIncrease;
   }
}
