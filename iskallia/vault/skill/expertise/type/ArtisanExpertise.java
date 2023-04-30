package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class ArtisanExpertise extends LearnableSkill {
   private float chanceToNotConsumePotential;

   public ArtisanExpertise(int unlockLevel, int learnPointCost, int regretPointCost, float chanceToNotConsumePotential) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.chanceToNotConsumePotential = chanceToNotConsumePotential;
   }

   public ArtisanExpertise() {
   }

   public float getChanceToNotConsumePotential() {
      return this.chanceToNotConsumePotential;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.chanceToNotConsumePotential), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.chanceToNotConsumePotential = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.chanceToNotConsumePotential)).ifPresent(tag -> nbt.put("chanceToNotConsumePotential", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.chanceToNotConsumePotential = Adapters.FLOAT.readNbt(nbt.get("chanceToNotConsumePotential")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.chanceToNotConsumePotential)).ifPresent(element -> json.add("chanceToNotConsumePotential", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.chanceToNotConsumePotential = Adapters.FLOAT.readJson(json.get("chanceToNotConsumePotential")).orElseThrow();
   }
}
