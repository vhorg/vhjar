package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;

public class TieredSkill extends LearnableSkill {
   private List<LearnableSkill> tiers;
   private int tier;
   private static final ArrayAdapter<Skill> TIERS = Adapters.ofArray(Skill[]::new, Adapters.SKILL);

   public TieredSkill(int unlockLevel, int learnPointCost, int regretPointCost, Stream<LearnableSkill> tiers) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.tiers = tiers.toList();
      this.tiers.forEach(tier -> tier.setParent(this));
   }

   public TieredSkill() {
   }

   public int getTier() {
      return this.tier;
   }

   public int getMaxTier() {
      return this.tiers.size();
   }

   public LearnableSkill getChild() {
      return this.getChild(this.tier);
   }

   public LearnableSkill getChild(int tier) {
      return tier > 0 ? this.tiers.get(tier - 1) : null;
   }

   @Override
   public int getUnlockLevel() {
      return this.tier >= this.tiers.size() ? 0 : this.tiers.get(this.tier).getUnlockLevel();
   }

   @Override
   public int getLearnPointCost() {
      return this.tier >= this.tiers.size() ? 0 : this.tiers.get(this.tier).getLearnPointCost();
   }

   @Override
   public int getSpentLearnPoints() {
      return this.getSpentLearnPoints(this.tier);
   }

   public int getSpentLearnPoints(int tier) {
      int points = 0;

      for (int i = 0; i < tier; i++) {
         points += this.tiers.get(i).getLearnPointCost();
      }

      return points;
   }

   @Override
   public int getRegretPointCost() {
      return this.tier <= 0 ? 0 : this.tiers.get(this.tier - 1).getRegretPointCost();
   }

   @Override
   public boolean isUnlocked() {
      return this.tier > 0;
   }

   @Override
   public boolean canLearn(SkillContext context) {
      return this.tier < this.tiers.size() && this.tiers.get(this.tier).canLearn(context);
   }

   @Override
   public void learn(SkillContext context) {
      if (this.tier > 0) {
         this.tiers.get(this.tier - 1).regret(SkillContext.empty());
      }

      this.tiers.get(this.tier++).learn(context);
   }

   @Override
   public boolean canRegret(SkillContext context) {
      return this.tier > 0 && this.tiers.get(this.tier - 1).canRegret(context);
   }

   @Override
   public void regret(SkillContext context) {
      this.tiers.get(--this.tier).regret(context);
      if (this.tier > 0) {
         this.tiers.get(this.tier - 1).learn(SkillContext.empty());
      }
   }

   @Override
   public Optional<Skill> getForId(String id) {
      return super.getForId(id).or(() -> {
         for (Skill child : this.tiers) {
            Skill skill = child.getForId(id).orElse(null);
            if (skill != null) {
               return Optional.of(skill);
            }
         }

         return Optional.empty();
      });
   }

   @Override
   public <T> void iterate(Class<T> type, Consumer<T> action) {
      super.iterate(type, action);

      for (LearnableSkill child : this.tiers) {
         child.iterate(type, action);
      }
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      other = super.mergeFrom(other, context);
      if (other instanceof TieredSkill tiered && this.getSpentLearnPoints() - tiered.getSpentLearnPoints(this.tier) >= 0) {
         context.setLearnPoints(context.getLearnPoints() + this.getSpentLearnPoints() - tiered.getSpentLearnPoints(this.tier));
         List<LearnableSkill> copy = new ArrayList<>();

         for (int i = 0; i < tiered.tiers.size(); i++) {
            Skill merging = i >= this.tiers.size() ? null : this.tiers.get(i);
            Skill merged;
            if (merging != null) {
               merged = merging.mergeFrom(tiered.tiers.get(i), context);
            } else {
               merged = tiered.tiers.get(i).copy();
            }

            if (merged instanceof LearnableSkill) {
               merged.setParent(this);
               copy.add((LearnableSkill)merged);
            }
         }

         this.tier = this.tier > copy.size() ? 0 : this.tier;
         this.tiers = copy;
         return this;
      } else {
         context.setLearnPoints(context.getLearnPoints() + this.getSpentLearnPoints());
         return other.copy();
      }
   }

   @Override
   public <T extends Skill> T copy() {
      TieredSkill copy = new TieredSkill(this.getUnlockLevel(), this.getLearnPointCost(), this.getRegretPointCost(), this.tiers.stream().map(Skill::copy));
      copy.parent = this.parent;
      copy.id = this.id;
      copy.name = this.name;
      copy.present = this.present;
      copy.learnPointCost = this.learnPointCost;
      copy.regretPointCost = this.regretPointCost;
      copy.unlockLevel = this.unlockLevel;
      copy.tier = this.tier;
      return (T)copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      TIERS.writeBits(this.tiers.toArray(Skill[]::new), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.tier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.tiers = Arrays.stream(TIERS.readBits(buffer).orElseThrow()).map(skill -> (LearnableSkill)skill).toList();
      this.tier = Adapters.INT.readBits(buffer).orElse(0);
      this.tiers.forEach(tier -> tier.setParent(this));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         TIERS.writeNbt(this.tiers.toArray(Skill[]::new)).ifPresent(tag -> nbt.put("tiers", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.tier)).ifPresent(tag -> nbt.put("tier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.tiers = Arrays.stream(TIERS.readNbt(nbt.get("tiers")).orElseThrow()).map(skill -> (LearnableSkill)skill).toList();
      this.tier = Adapters.INT.readNbt(nbt.get("tier")).orElse(0);
      this.tiers.forEach(tier -> tier.setParent(this));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         TIERS.writeJson(this.tiers.toArray(Skill[]::new)).ifPresent(element -> json.add("tiers", element));
         Adapters.INT.writeJson(Integer.valueOf(this.tier)).ifPresent(element -> json.add("tier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.tiers = Arrays.stream(TIERS.readJson(json.get("tiers")).orElseThrow()).map(skill -> (LearnableSkill)skill).toList();
      this.tier = Adapters.INT.readJson(json.get("tier")).orElse(0);
      this.tiers.forEach(tier -> tier.setParent(this));
   }
}
