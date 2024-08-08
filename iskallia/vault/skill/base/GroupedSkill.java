package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;

public class GroupedSkill extends LearnableSkill {
   private List<LearnableSkill> children;
   private int selected;
   private int maxSpentLearnPoints;
   private static final ArrayAdapter<Skill> SPECIALIZATIONS = Adapters.ofArray(Skill[]::new, Adapters.SKILL);

   public GroupedSkill(int unlockLevel, int learnPointCost, int regretPointCost, Stream<LearnableSkill> children, int maxSpentLearnPoints) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.children = children.toList();
      this.children.forEach(specialization -> specialization.setParent(this));
      this.maxSpentLearnPoints = maxSpentLearnPoints;
   }

   public GroupedSkill() {
   }

   public List<LearnableSkill> getChildren() {
      return this.children;
   }

   public int getMaxSpentLearnPoints() {
      return this.maxSpentLearnPoints;
   }

   @Override
   public int getUnlockLevel() {
      return this.children.get(this.selected).getUnlockLevel();
   }

   @Override
   public int getLearnPointCost() {
      return this.children.get(this.selected).getLearnPointCost();
   }

   @Override
   public int getRegretPointCost() {
      return this.children.get(this.selected).getRegretPointCost();
   }

   @Override
   public boolean isUnlocked() {
      return this.children.stream().anyMatch(Skill::isUnlocked);
   }

   @Override
   public int getSpentLearnPoints() {
      int points = 0;

      for (LearnableSkill child : this.children) {
         points += child.getSpentLearnPoints();
      }

      return points;
   }

   @Override
   public boolean canLearn(SkillContext context) {
      int previous = context.getLearnPoints();
      context.setLearnPoints(Math.min(previous, this.maxSpentLearnPoints - this.getSpentLearnPoints()));
      boolean result = this.children.get(this.selected).canLearn(context);
      context.setLearnPoints(previous);
      return result;
   }

   @Override
   public void learn(SkillContext context) {
      this.children.get(this.selected).learn(context);
   }

   @Override
   public boolean canRegret(SkillContext context) {
      return this.children.get(this.selected).canRegret(context);
   }

   @Override
   public void regret(SkillContext context) {
      this.children.get(this.selected).regret(context);
   }

   public void select(String id) {
      for (int i = 0; i < this.children.size(); i++) {
         LearnableSkill child = this.children.get(i);
         if (child.getId().equals(id)) {
            this.selected = i;
            break;
         }
      }
   }

   @Override
   public Optional<Skill> getForId(String id) {
      return super.getForId(id).or(() -> {
         for (Skill child : this.children) {
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

      for (LearnableSkill child : this.children) {
         child.iterate(type, action);
      }
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      int spentPoints = this.getSpentLearnPoints();
      if (other instanceof GroupedSkill grouped && spentPoints <= grouped.getMaxSpentLearnPoints()) {
         Map<String, Skill> idToSkill = new LinkedHashMap<>();
         Map<String, Integer> idToIndex = new LinkedHashMap<>();

         for (int index = 0; index < grouped.children.size(); index++) {
            Skill skill = grouped.children.get(index);
            idToSkill.put(skill.getId(), skill);
            idToIndex.put(skill.getId(), index);
         }

         for (Skill child : this.children) {
            Skill merged;
            if (child.getId() != null && idToSkill.containsKey(child.getId())) {
               merged = child.mergeFrom(idToSkill.get(child.getId()), context);
            } else {
               merged = child.mergeFrom(null, context);
            }

            if (idToIndex.containsKey(child.getId()) && merged instanceof LearnableSkill learnable) {
               grouped.children.set(idToIndex.get(child.getId()), learnable);
            }
         }

         grouped.selected = this.selected;
         return other;
      } else {
         context.setLearnPoints(context.getLearnPoints() + spentPoints);
         return other;
      }
   }

   @Override
   public <T extends Skill> T copy() {
      GroupedSkill copy = new GroupedSkill(
         this.getUnlockLevel(), this.getLearnPointCost(), this.getRegretPointCost(), this.children.stream().map(Skill::copy), this.maxSpentLearnPoints
      );
      copy.parent = this.parent;
      copy.id = this.id;
      copy.name = this.name;
      copy.present = this.present;
      copy.learnPointCost = this.learnPointCost;
      copy.regretPointCost = this.regretPointCost;
      copy.unlockLevel = this.unlockLevel;
      copy.selected = this.selected;
      return (T)copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      SPECIALIZATIONS.writeBits(this.children.toArray(Skill[]::new), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.selected), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.maxSpentLearnPoints), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.children = Arrays.stream(SPECIALIZATIONS.readBits(buffer).orElseThrow()).map(skill -> (LearnableSkill)skill).collect(Collectors.toList());
      this.selected = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.maxSpentLearnPoints = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.children.forEach(specialization -> specialization.setParent(this));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         SPECIALIZATIONS.writeNbt(this.children.toArray(Skill[]::new)).ifPresent(tag -> nbt.put("children", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.selected)).ifPresent(tag -> nbt.put("selected", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxSpentLearnPoints)).ifPresent(tag -> nbt.put("maxSpentLearnPoints", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.children = Arrays.stream(SPECIALIZATIONS.readNbt(nbt.get("children")).orElseThrow())
         .map(skill -> (LearnableSkill)skill)
         .collect(Collectors.toList());
      this.selected = Adapters.INT.readNbt(nbt.get("selected")).orElse(0);
      this.maxSpentLearnPoints = Adapters.INT.readNbt(nbt.get("maxSpentLearnPoints")).orElse(0);
      this.children.forEach(child -> child.setParent(this));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         SPECIALIZATIONS.writeJson(this.children.toArray(Skill[]::new)).ifPresent(element -> json.add("children", element));
         Adapters.INT.writeJson(Integer.valueOf(this.selected)).ifPresent(element -> json.add("selected", element));
         Adapters.INT.writeJson(Integer.valueOf(this.maxSpentLearnPoints)).ifPresent(element -> json.add("maxSpentLearnPoints", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.children = Arrays.stream(SPECIALIZATIONS.readJson(json.get("children")).orElseThrow())
         .map(skill -> (LearnableSkill)skill)
         .collect(Collectors.toList());
      this.selected = Adapters.INT.readJson(json.get("selected")).orElse(0);
      this.maxSpentLearnPoints = Adapters.INT.readJson(json.get("maxSpentLearnPoints")).orElse(0);
      this.children.forEach(child -> child.setParent(this));
   }
}
