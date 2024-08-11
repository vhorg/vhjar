package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.Cooldown;
import iskallia.vault.skill.ability.effect.spi.core.CooldownSkill;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;

public class SpecializedSkill extends LearnableSkill implements CooldownSkill {
   private List<LearnableSkill> specializations;
   private int index;
   private static final ArrayAdapter<Skill> SPECIALIZATIONS = Adapters.ofArray(Skill[]::new, Adapters.SKILL);

   public SpecializedSkill(int unlockLevel, int learnPointCost, int regretPointCost, Stream<LearnableSkill> children) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.specializations = children.toList();
      this.specializations.forEach(specialization -> specialization.setParent(this));
   }

   public SpecializedSkill() {
   }

   public int getIndex() {
      return this.index;
   }

   @Override
   public int getUnlockLevel() {
      return this.specializations.get(this.index).getUnlockLevel();
   }

   @Override
   public int getLearnPointCost() {
      return this.specializations.get(this.index).getLearnPointCost();
   }

   @Override
   public int getRegretPointCost() {
      return this.specializations.get(this.index).getRegretPointCost();
   }

   @Override
   public int getSpentLearnPoints() {
      return this.specializations.get(this.index).getSpentLearnPoints();
   }

   @Override
   public boolean isUnlocked() {
      return this.specializations.get(this.index).isUnlocked();
   }

   @Override
   public boolean canLearn(SkillContext context) {
      return this.specializations.get(this.index).canLearn(context);
   }

   @Override
   public void learn(SkillContext context) {
      this.specializations.get(this.index).learn(context);
   }

   @Override
   public boolean canRegret(SkillContext context) {
      return this.specializations.get(this.index).canRegret(context);
   }

   @Override
   public void regret(SkillContext context) {
      this.specializations.get(this.index).regret(context);
   }

   public LearnableSkill getSpecialization() {
      return this.specializations.get(this.index);
   }

   public LearnableSkill getSpecialization(int index) {
      return this.specializations.get(0);
   }

   public List<LearnableSkill> getSpecializations() {
      return this.specializations;
   }

   public void resetSpecialization(SkillContext context) {
      this.specialize(0, context);
   }

   public void specialize(int index, SkillContext context) {
      if (this.index != index) {
         LearnableSkill current = this.specializations.get(this.index);
         if (current instanceof TieredSkill tiered) {
            int tier = tiered.getUnmodifiedTier();

            for (int i = 0; i < tier; i++) {
               current.regret(context);
            }

            for (int i = 0; i < tier; i++) {
               if (this.specializations.get(index).canLearn(context)) {
                  this.specializations.get(index).learn(context);
               }
            }
         } else {
            int tier;
            for (tier = 0; current.isUnlocked(); tier++) {
               current.regret(context);
            }

            for (int ix = 0; ix < tier; ix++) {
               if (this.specializations.get(index).canLearn(context)) {
                  this.specializations.get(index).learn(context);
               }
            }
         }
      }

      this.index = index;
   }

   public void specialize(String id, SkillContext context) {
      for (int i = 0; i < this.specializations.size(); i++) {
         LearnableSkill specialization = this.specializations.get(i);
         if (specialization.getId().equals(id)) {
            this.specialize(i, context);
            break;
         }
      }
   }

   public int indexOf(String id) {
      List<LearnableSkill> learnableSkills = this.specializations;

      for (int i = 0; i < learnableSkills.size(); i++) {
         LearnableSkill specialization = learnableSkills.get(i);
         if (specialization.getId().equals(id)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public Optional<Skill> getForId(String id) {
      return super.getForId(id).or(() -> {
         for (Skill specialization : this.specializations) {
            Skill skill = specialization.getForId(id).orElse(null);
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

      for (LearnableSkill specialization : this.specializations) {
         specialization.iterate(type, action);
      }
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      int spentPoints = this.getSpentLearnPoints();
      if (!(other instanceof SpecializedSkill specialized)) {
         context.setLearnPoints(context.getLearnPoints() + spentPoints);
         return other;
      } else {
         LinkedHashMap idToSkill = new LinkedHashMap();
         LinkedHashMap idToIndex = new LinkedHashMap();

         for (int selected = 0; selected < specialized.specializations.size(); selected++) {
            Skill skill = specialized.specializations.get(selected);
            idToSkill.put(skill.getId(), skill);
            idToIndex.put(skill.getId(), selected);
         }

         for (Skill child : this.specializations) {
            Skill merged;
            if (child.getId() != null && idToSkill.containsKey(child.getId())) {
               merged = child.mergeFrom((Skill)idToSkill.get(child.getId()), context);
            } else {
               merged = child.mergeFrom(null, context);
            }

            if (idToIndex.containsKey(child.getId()) && merged instanceof LearnableSkill learnable) {
               specialized.specializations.set((Integer)idToIndex.get(child.getId()), learnable);
            }
         }

         String selected = this.specializations.get(this.index).getId();
         if (selected != null && idToIndex.containsKey(selected)) {
            specialized.index = (Integer)idToIndex.get(selected);
         } else {
            specialized.index = 0;
         }

         return other;
      }
   }

   @Override
   public Optional<Cooldown> getCooldown() {
      Cooldown max = null;

      for (LearnableSkill child : this.specializations) {
         if (child instanceof CooldownSkill) {
            Cooldown cooldown = ((CooldownSkill)child).getCooldown().orElse(null);
            if (cooldown != null && (max == null || cooldown.isLargerThan(max))) {
               max = cooldown;
            }
         }
      }

      return Optional.ofNullable(max);
   }

   @Override
   public void putOnCooldown(int cooldownDelayTicks, SkillContext context) {
      LearnableSkill child = this.getSpecialization();
      if (child instanceof CooldownSkill) {
         ((CooldownSkill)child).putOnCooldown(cooldownDelayTicks, context);
      }
   }

   @Override
   public <T extends Skill> T copy() {
      SpecializedSkill copy = new SpecializedSkill(this.unlockLevel, this.learnPointCost, this.regretPointCost, this.specializations.stream().map(Skill::copy));
      copy.parent = this.parent;
      copy.id = this.id;
      copy.name = this.name;
      copy.present = this.present;
      copy.learnPointCost = this.learnPointCost;
      copy.regretPointCost = this.regretPointCost;
      copy.unlockLevel = this.unlockLevel;
      copy.index = this.index;
      return (T)copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      SPECIALIZATIONS.writeBits(this.specializations.toArray(Skill[]::new), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.index), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.specializations = Arrays.stream(SPECIALIZATIONS.readBits(buffer).orElseThrow()).map(skill -> (LearnableSkill)skill).collect(Collectors.toList());
      this.index = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.specializations.forEach(specialization -> specialization.setParent(this));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         SPECIALIZATIONS.writeNbt(this.specializations.toArray(Skill[]::new)).ifPresent(tag -> nbt.put("specializations", tag));
         Adapters.INT_SEGMENTED_3.writeNbt(Integer.valueOf(this.index)).ifPresent(tag -> nbt.put("index", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.specializations = Arrays.stream(SPECIALIZATIONS.readNbt(nbt.get("specializations")).orElseThrow())
         .map(skill -> (LearnableSkill)skill)
         .collect(Collectors.toList());
      this.index = Adapters.INT_SEGMENTED_3.readNbt(nbt.get("index")).orElse(0);
      this.specializations.forEach(specialization -> specialization.setParent(this));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         SPECIALIZATIONS.writeJson(this.specializations.toArray(Skill[]::new)).ifPresent(element -> json.add("specializations", element));
         Adapters.INT.writeJson(Integer.valueOf(this.index)).ifPresent(element -> json.add("index", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.specializations = Arrays.stream(SPECIALIZATIONS.readJson(json.get("specializations")).orElseThrow())
         .map(skill -> (LearnableSkill)skill)
         .collect(Collectors.toList());
      this.index = Adapters.INT.readJson(json.get("index")).orElse(0);
      this.specializations.forEach(specialization -> specialization.setParent(this));
   }
}
