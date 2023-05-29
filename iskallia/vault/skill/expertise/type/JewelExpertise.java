package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class JewelExpertise extends LearnableSkill {
   private float modifierChanceReduction;

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.modifierChanceReduction), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.modifierChanceReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.modifierChanceReduction)).ifPresent(tag -> nbt.put("modifierChanceReduction", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.modifierChanceReduction = Adapters.FLOAT.readNbt(nbt.get("modifierChanceReduction")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.modifierChanceReduction)).ifPresent(element -> json.add("modifierChanceReduction", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.modifierChanceReduction = Adapters.FLOAT.readJson(json.get("modifierChanceReduction")).orElseThrow();
   }

   public float getModifierChanceReduction() {
      return this.modifierChanceReduction;
   }
}
