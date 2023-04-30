package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class JavelinDamageTalent extends LearnableSkill {
   private float percentAttackDamageDealt;

   public JavelinDamageTalent(int unlockLevel, int learnPointCost, int regretPointCost, float percentAttackDamageDealt) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.percentAttackDamageDealt = percentAttackDamageDealt;
   }

   public JavelinDamageTalent() {
   }

   public float getIncreasedDamage() {
      return this.percentAttackDamageDealt;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentAttackDamageDealt), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.percentAttackDamageDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentAttackDamageDealt)).ifPresent(tag -> nbt.put("percentAttackDamageDealt", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.percentAttackDamageDealt = Adapters.FLOAT.readNbt(nbt.get("percentAttackDamageDealt")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentAttackDamageDealt)).ifPresent(element -> json.add("percentAttackDamageDealt", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.percentAttackDamageDealt = Adapters.FLOAT.readJson(json.get("percentAttackDamageDealt")).orElseThrow();
   }
}
