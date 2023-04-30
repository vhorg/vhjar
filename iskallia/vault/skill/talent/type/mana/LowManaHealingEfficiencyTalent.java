package iskallia.vault.skill.talent.type.mana;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;

public class LowManaHealingEfficiencyTalent extends LowManaTalent {
   private float additionalHealingEfficiency;

   public LowManaHealingEfficiencyTalent(
      int unlockLevel, int learnPointCost, int regretPointCost, MobEffect effect, float manaThreshold, float additionalHealingEfficiency
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, effect, manaThreshold);
      this.additionalHealingEfficiency = additionalHealingEfficiency;
   }

   public LowManaHealingEfficiencyTalent() {
   }

   public float getAdditionalHealingEfficiency() {
      return this.additionalHealingEfficiency;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalHealingEfficiency), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalHealingEfficiency = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalHealingEfficiency)).ifPresent(tag -> nbt.put("additionalHealingEfficiency", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalHealingEfficiency = Adapters.FLOAT.readNbt(nbt.get("additionalHealingEfficiency")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.additionalHealingEfficiency)).ifPresent(element -> json.add("additionalHealingEfficiency", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalHealingEfficiency = Adapters.FLOAT.readJson(json.get("additionalHealingEfficiency")).orElseThrow();
   }
}
