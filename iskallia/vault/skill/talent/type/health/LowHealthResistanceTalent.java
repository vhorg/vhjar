package iskallia.vault.skill.talent.type.health;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;

public class LowHealthResistanceTalent extends LowHealthTalent {
   private float additionalResistance;

   public LowHealthResistanceTalent(
      int unlockLevel, int learnPointCost, int regretPointCost, float healthThreshold, MobEffect effect, float additionalResistance
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, healthThreshold, effect);
      this.additionalResistance = additionalResistance;
   }

   public LowHealthResistanceTalent() {
   }

   public float getAdditionalResistance() {
      return this.additionalResistance;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalResistance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalResistance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalResistance)).ifPresent(tag -> nbt.put("additionalResistance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalResistance = Adapters.FLOAT.readNbt(nbt.get("additionalResistance")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.additionalResistance)).ifPresent(element -> json.add("additionalResistance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalResistance = Adapters.FLOAT.readJson(json.get("additionalResistance")).orElseThrow();
   }
}
