package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class LearnableSkill extends Skill {
   protected int unlockLevel;
   protected int learnPointCost;
   protected int regretPointCost;

   public LearnableSkill(int unlockLevel, int learnPointCost, int regretPointCost) {
      this.unlockLevel = unlockLevel;
      this.learnPointCost = learnPointCost;
      this.regretPointCost = regretPointCost;
   }

   protected LearnableSkill() {
   }

   public int getUnlockLevel() {
      return this.unlockLevel;
   }

   public int getLearnPointCost() {
      return this.learnPointCost;
   }

   public int getRegretPointCost() {
      return this.regretPointCost;
   }

   public int getSpentLearnPoints() {
      return this.isUnlocked() ? this.learnPointCost : 0;
   }

   public boolean canLearn(SkillContext context) {
      return context.getLevel() >= this.unlockLevel && context.getLearnPoints() >= this.learnPointCost;
   }

   public void learn(SkillContext context) {
      context.setLearnPoints(context.getLearnPoints() - this.learnPointCost);
      this.setPresent(true, context);
   }

   public boolean canRegret(SkillContext context) {
      return context.getRegretPoints() >= this.regretPointCost;
   }

   public void regret(SkillContext context) {
      context.setLearnPoints(context.getLearnPoints() + this.learnPointCost);
      context.setRegretPoints(context.getRegretPoints() - this.regretPointCost);
      this.setPresent(false, context);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.unlockLevel), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.learnPointCost), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.regretPointCost), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.unlockLevel = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.learnPointCost = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.regretPointCost = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.unlockLevel)).ifPresent(tag -> nbt.put("unlockLevel", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.learnPointCost)).ifPresent(tag -> nbt.put("learnPointCost", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.regretPointCost)).ifPresent(tag -> nbt.put("regretPointCost", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.unlockLevel = Adapters.INT.readNbt(nbt.get("unlockLevel")).orElse(0);
      this.learnPointCost = Adapters.INT.readNbt(nbt.get("learnPointCost")).orElse(0);
      this.regretPointCost = Adapters.INT.readNbt(nbt.get("regretPointCost")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.unlockLevel)).ifPresent(element -> json.add("unlockLevel", element));
         Adapters.INT.writeJson(Integer.valueOf(this.learnPointCost)).ifPresent(element -> json.add("learnPointCost", element));
         Adapters.INT.writeJson(Integer.valueOf(this.regretPointCost)).ifPresent(element -> json.add("regretPointCost", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.unlockLevel = Adapters.INT.readJson(json.get("unlockLevel")).orElse(0);
      this.learnPointCost = Adapters.INT.readJson(json.get("learnPointCost")).orElse(0);
      this.regretPointCost = Adapters.INT.readJson(json.get("regretPointCost")).orElse(0);
   }
}
