package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class TrinketerExpertise extends LearnableSkill {
   private float damageAvoidanceChance;

   public float getDamageAvoidanceChance() {
      return this.damageAvoidanceChance;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageAvoidanceChance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageAvoidanceChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageAvoidanceChance)).ifPresent(tag -> nbt.put("damageAvoidanceChance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageAvoidanceChance = Adapters.FLOAT.readNbt(nbt.get("damageAvoidanceChance")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageAvoidanceChance)).ifPresent(element -> json.add("damageAvoidanceChance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageAvoidanceChance = Adapters.FLOAT.readJson(json.get("damageAvoidanceChance")).orElseThrow();
   }
}
