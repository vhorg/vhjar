package iskallia.vault.skill.tree;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TickingSkill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;

public class SkillTree extends LearnableSkill implements TickingSkill {
   public List<Skill> skills = new ArrayList<>();
   private static final ArrayAdapter<Skill> SKILLS = Adapters.ofArray(Skill[]::new, Adapters.SKILL);

   @Override
   public int getSpentLearnPoints() {
      int points = 0;

      for (Skill skill : this.skills) {
         if (skill instanceof LearnableSkill) {
            points += ((LearnableSkill)skill).getSpentLearnPoints();
         }
      }

      return points;
   }

   @Override
   public Optional<Skill> getForId(String id) {
      return super.getForId(id).or(() -> {
         for (Skill child : this.skills) {
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

      for (Skill child : this.skills) {
         child.iterate(type, action);
      }
   }

   @Override
   public void onTick(SkillContext context) {
      this.iterate(TickingSkill.class, skill -> {
         if (skill != this) {
            skill.onTick(context);
         }
      });
   }

   public void learn(String id, SkillContext context) {
      this.getForId(id).ifPresent(skill -> {
         if (skill instanceof LearnableSkill learnable) {
            if (!learnable.canLearn(context)) {
               return;
            }

            learnable.learn(context);
         }
      });
   }

   public void regret(String id, SkillContext context) {
      this.getForId(id).ifPresent(skill -> {
         if (skill instanceof LearnableSkill learnable) {
            if (!learnable.canRegret(context)) {
               return;
            }

            learnable.regret(context);
         }
      });
   }

   public void specialize(String id, int index, SkillContext context) {
      this.getForId(id).ifPresent(skill -> {
         if (skill instanceof SpecializedSkill specialized) {
            specialized.specialize(index, context);
         }
      });
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      if (!(other instanceof SkillTree tree)) {
         context.setLearnPoints(context.getLearnPoints() + this.getSpentLearnPoints());
         return other;
      } else {
         LinkedHashMap idToSkill = new LinkedHashMap();
         LinkedHashMap idToIndex = new LinkedHashMap();

         for (int index = 0; index < tree.skills.size(); index++) {
            Skill skill = tree.skills.get(index);
            idToSkill.put(skill.getId(), skill);
            idToIndex.put(skill.getId(), index);
         }

         for (Skill child : this.skills) {
            Skill merged;
            if (child.getId() != null && idToSkill.containsKey(child.getId())) {
               merged = child.mergeFrom((Skill)idToSkill.get(child.getId()), context);
            } else {
               merged = child.mergeFrom(null, context);
            }

            if (idToIndex.containsKey(child.getId())) {
               tree.skills.set((Integer)idToIndex.get(child.getId()), merged);
            }
         }

         return tree;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      SKILLS.writeBits(this.skills.toArray(Skill[]::new), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.skills = Arrays.stream(SKILLS.readBits(buffer).orElseThrow()).collect(Collectors.toList());
      this.skills.forEach(skill -> skill.setParent(this));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         SKILLS.writeNbt(this.skills.toArray(Skill[]::new)).ifPresent(tag -> nbt.put("skills", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.skills = Arrays.stream(SKILLS.readNbt(nbt.get("skills")).orElseThrow()).collect(Collectors.toList());
      this.skills.forEach(skill -> skill.setParent(this));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         SKILLS.writeJson(this.skills.toArray(Skill[]::new)).ifPresent(element -> json.add("skills", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.skills = Arrays.stream(SKILLS.readJson(json.get("skills")).orElseThrow()).collect(Collectors.toList());
      this.skills.forEach(skill -> skill.setParent(this));
   }
}
