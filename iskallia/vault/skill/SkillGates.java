package iskallia.vault.skill;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.SkillTree;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SkillGates {
   @Expose
   private final Map<String, SkillGates.Entry> entries = new HashMap<>();

   public void addEntry(String skillName, SkillGates.Entry entry) {
      this.entries.put(skillName, entry);
   }

   public List<String> getDependencyAbilities(String abilityName) {
      List<String> abilities = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(abilityName);
      if (entry == null) {
         return abilities;
      } else {
         entry.dependsOn.forEach(dependencyName -> {
            Skill dependency = ModConfigs.ABILITIES.getAbilityById(dependencyName).orElseThrow();
            abilities.add(dependency.getId());
         });
         return abilities;
      }
   }

   public List<String> getLockedByAbilities(String abilityName) {
      List<String> abilities = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(abilityName);
      if (entry == null) {
         return abilities;
      } else {
         entry.lockedBy.forEach(dependencyName -> {
            Skill dependency = ModConfigs.ABILITIES.getAbilityById(dependencyName).orElseThrow();
            abilities.add(dependency.getId());
         });
         return abilities;
      }
   }

   public List<String> getAbilitiesDependingOn(String abilityName) {
      List<String> abilities = new LinkedList<>();
      Skill ability = ModConfigs.ABILITIES.getAbilityById(abilityName).orElseThrow();
      ModConfigs.ABILITIES.get().ifPresent(tree -> tree.iterate(Skill.class, skill -> {
         List<String> dependencies = ModConfigs.SKILL_GATES.getGates().getDependencyAbilities(skill.getId());
         if (dependencies.contains(ability.getId())) {
            abilities.add(skill.getId());
         }
      }));
      return abilities;
   }

   public List<String> getDependencySkills(String talentName) {
      List<String> talents = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(talentName);
      if (entry == null) {
         return talents;
      } else {
         talents.addAll(entry.dependsOn);
         return talents;
      }
   }

   public List<String> getLockedBySkills(String talentName) {
      List<String> talents = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(talentName);
      if (entry == null) {
         return talents;
      } else {
         talents.addAll(entry.lockedBy);
         return talents;
      }
   }

   public List<String> getSkillsDependingOn(String talentName, SkillTree tree) {
      List<String> talents = new LinkedList<>();
      tree.iterate(Skill.class, skill -> {
         List<String> dependencies = ModConfigs.SKILL_GATES.getGates().getDependencySkills(skill.getId());
         if (dependencies.contains(talentName)) {
            talents.add(skill.getId());
         }
      });
      return talents;
   }

   public List<Research> getDependencyResearches(String researchName) {
      List<Research> researches = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(researchName);
      if (entry == null) {
         return researches;
      } else {
         entry.dependsOn.forEach(dependencyName -> {
            Research dependency = ModConfigs.RESEARCHES.getByName(dependencyName);
            researches.add(dependency);
         });
         return researches;
      }
   }

   public List<Research> getLockedByResearches(String researchName) {
      List<Research> researches = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(researchName);
      if (entry == null) {
         return researches;
      } else {
         entry.lockedBy.forEach(dependencyName -> {
            Research dependency = ModConfigs.RESEARCHES.getByName(dependencyName);
            researches.add(dependency);
         });
         return researches;
      }
   }

   public boolean isLocked(String researchName, ResearchTree researchTree) {
      SkillGates gates = ModConfigs.SKILL_GATES.getGates();
      List<String> researchesDone = researchTree.getResearchesDone();

      for (Research dependencyResearch : gates.getDependencyResearches(researchName)) {
         if (!researchesDone.contains(dependencyResearch.getName())) {
            return true;
         }
      }

      for (Research lockedByResearch : gates.getLockedByResearches(researchName)) {
         if (researchesDone.contains(lockedByResearch.getName())) {
            return true;
         }
      }

      return false;
   }

   public boolean isLocked(String skill, SkillTree tree) {
      SkillGates gates = ModConfigs.SKILL_GATES.getGates();

      for (String dependencyTalent : gates.getDependencySkills(skill)) {
         if (!tree.getForId(dependencyTalent).map(Skill::isUnlocked).orElse(false)) {
            return true;
         }
      }

      for (String lockedByTalent : gates.getLockedBySkills(skill)) {
         if (tree.getForId(lockedByTalent).map(Skill::isUnlocked).orElse(false)) {
            return true;
         }
      }

      return false;
   }

   public boolean shouldDrawArrow(String entryA, String entryB) {
      SkillGates.Entry entry = this.entries.get(entryA);
      if (entry == null) {
         return false;
      } else {
         return !entry.dependsOn.contains(entryB) && !entry.lockedBy.contains(entryB) ? false : !entry.ignoreArrow;
      }
   }

   public static class Entry {
      @Expose
      private List<String> dependsOn = new LinkedList<>();
      @Expose
      private List<String> lockedBy = new LinkedList<>();
      @Expose
      private boolean ignoreArrow;

      public void setDependsOn(String... skills) {
         this.dependsOn.addAll(Arrays.asList(skills));
      }

      public void setLockedBy(String... skills) {
         this.lockedBy.addAll(Arrays.asList(skills));
      }
   }
}
