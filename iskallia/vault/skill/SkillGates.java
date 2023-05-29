package iskallia.vault.skill;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.skillgate.ConstantSkillGate;
import iskallia.vault.config.skillgate.EitherSkillGate;
import iskallia.vault.config.skillgate.SkillGateType;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.SkillTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SkillGates {
   public static TypeSupplierAdapter<SkillGateType> GATE_TYPE = new TypeSupplierAdapter<ConstantSkillGate>("type", false)
      .<TypeSupplierAdapter<EitherSkillGate>>register("constant", ConstantSkillGate.class, ConstantSkillGate::new)
      .register("either", EitherSkillGate.class, EitherSkillGate::new);
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
         for (Skill ability : ModConfigs.ABILITIES.tree.skills) {
            if (entry.dependsOn.stream().anyMatch(l -> l.allows(ability.getId()))) {
               abilities.add(ability.getId());
            }
         }

         return abilities;
      }
   }

   public List<String> getLockedByAbilities(String abilityName) {
      List<String> abilities = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(abilityName);
      if (entry == null) {
         return abilities;
      } else {
         for (Skill ability : ModConfigs.ABILITIES.tree.skills) {
            if (entry.lockedBy.stream().anyMatch(l -> l.allows(ability.getId()))) {
               abilities.add(ability.getId());
            }
         }

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

   private List<String> getAllSkillIds() {
      List<String> allSkillIds = new ArrayList<>();
      ModConfigs.TALENTS.tree.skills.forEach(s -> allSkillIds.add(s.getId()));
      ModConfigs.ABILITIES.tree.skills.forEach(s -> allSkillIds.add(s.getId()));
      ModConfigs.EXPERTISES.getAll().skills.forEach(s -> allSkillIds.add(s.getId()));
      return allSkillIds;
   }

   public List<String> getDependencySkills(String skillName) {
      List<String> skills = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(skillName);
      if (entry == null) {
         return skills;
      } else {
         for (String skillId : this.getAllSkillIds()) {
            if (entry.dependsOn.stream().anyMatch(l -> l.allows(skillId))) {
               skills.add(skillId);
            }
         }

         return skills;
      }
   }

   public List<String> getLockedBySkills(String skillName) {
      List<String> skills = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(skillName);
      if (entry == null) {
         return skills;
      } else {
         for (String skillId : this.getAllSkillIds()) {
            if (entry.lockedBy.stream().anyMatch(l -> l.allows(skillId))) {
               skills.add(skillId);
            }
         }

         return skills;
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
         for (Research research : ModConfigs.RESEARCHES.getAll()) {
            if (entry.dependsOn.stream().anyMatch(l -> l.allows(research.getName()))) {
               researches.add(research);
            }
         }

         return researches;
      }
   }

   public List<Research> getLockedByResearches(String researchName) {
      List<Research> researches = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(researchName);
      if (entry == null) {
         return researches;
      } else {
         for (Research research : ModConfigs.RESEARCHES.getAll()) {
            if (entry.lockedBy.stream().anyMatch(l -> l.allows(research.getName()))) {
               researches.add(research);
            }
         }

         return researches;
      }
   }

   public boolean isLocked(String researchName, ResearchTree researchTree) {
      SkillGates gates = ModConfigs.SKILL_GATES.getGates();
      List<String> researchesDone = researchTree.getResearchesDone();
      SkillGates.Entry gateEntries = gates.entries.get(researchName);
      if (gateEntries == null) {
         return false;
      } else {
         for (SkillGateType dependencyGate : gateEntries.dependsOn) {
            if (researchesDone.stream().noneMatch(dependencyGate::allows)) {
               return true;
            }
         }

         for (SkillGateType lockGate : gateEntries.lockedBy) {
            if (researchesDone.stream().anyMatch(lockGate::allows)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isLocked(String skill, SkillTree tree) {
      SkillGates gates = ModConfigs.SKILL_GATES.getGates();
      SkillGates.Entry gateEntries = gates.entries.get(skill);
      if (gateEntries == null) {
         return false;
      } else {
         List<Skill> unlockedSkills = tree.skills.stream().filter(Skill::isUnlocked).toList();

         for (SkillGateType dependencyGate : gateEntries.dependsOn) {
            if (unlockedSkills.stream().map(Skill::getId).noneMatch(dependencyGate::allows)) {
               return true;
            }
         }

         for (SkillGateType lockGate : gateEntries.lockedBy) {
            if (unlockedSkills.stream().map(Skill::getId).anyMatch(lockGate::allows)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean shouldDrawArrow(String entryA, String entryB) {
      SkillGates.Entry entry = this.entries.get(entryA);
      if (entry == null) {
         return false;
      } else {
         boolean depends = entry.dependsOn.stream().anyMatch(e -> e.allows(entryB));
         boolean locks = entry.lockedBy.stream().anyMatch(e -> e.allows(entryB));
         return !depends && !locks ? false : !entry.ignoreArrow;
      }
   }

   public static class Entry {
      @Expose
      private List<SkillGateType> dependsOn = new LinkedList<>();
      @Expose
      private List<SkillGateType> lockedBy = new LinkedList<>();
      @Expose
      private boolean ignoreArrow;

      public void setDependsOn(SkillGateType... skills) {
         this.dependsOn.addAll(Arrays.asList(skills));
      }

      public void setLockedBy(SkillGateType... skills) {
         this.lockedBy.addAll(Arrays.asList(skills));
      }
   }
}
