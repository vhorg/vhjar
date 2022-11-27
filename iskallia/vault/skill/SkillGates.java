package iskallia.vault.skill;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentTree;
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

   public List<AbilityGroup<?, ?>> getDependencyAbilities(String abilityName) {
      List<AbilityGroup<?, ?>> abilities = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(abilityName);
      if (entry == null) {
         return abilities;
      } else {
         entry.dependsOn.forEach(dependencyName -> {
            AbilityGroup<?, ?> dependency = ModConfigs.ABILITIES.getAbilityGroupByName(dependencyName);
            abilities.add(dependency);
         });
         return abilities;
      }
   }

   public List<AbilityGroup<?, ?>> getLockedByAbilities(String abilityName) {
      List<AbilityGroup<?, ?>> abilities = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(abilityName);
      if (entry == null) {
         return abilities;
      } else {
         entry.lockedBy.forEach(dependencyName -> {
            AbilityGroup<?, ?> dependency = ModConfigs.ABILITIES.getAbilityGroupByName(dependencyName);
            abilities.add(dependency);
         });
         return abilities;
      }
   }

   public List<AbilityGroup<?, ?>> getAbilitiesDependingOn(String abilityName) {
      List<AbilityGroup<?, ?>> abilities = new LinkedList<>();
      AbilityGroup<?, ?> ability = ModConfigs.ABILITIES.getAbilityGroupByName(abilityName);

      for (AbilityGroup<?, ?> otherAbility : ModConfigs.ABILITIES.getAll()) {
         List<AbilityGroup<?, ?>> dependencies = ModConfigs.SKILL_GATES.getGates().getDependencyAbilities(otherAbility.getParentName());
         if (dependencies.contains(ability)) {
            abilities.add(otherAbility);
         }
      }

      return abilities;
   }

   public List<TalentGroup<?>> getDependencyTalents(String talentName) {
      List<TalentGroup<?>> talents = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(talentName);
      if (entry == null) {
         return talents;
      } else {
         entry.dependsOn.forEach(dependencyName -> {
            TalentGroup<?> dependency = ModConfigs.TALENTS.getByName(dependencyName);
            talents.add(dependency);
         });
         return talents;
      }
   }

   public List<TalentGroup<?>> getLockedByTalents(String talentName) {
      List<TalentGroup<?>> talents = new LinkedList<>();
      SkillGates.Entry entry = this.entries.get(talentName);
      if (entry == null) {
         return talents;
      } else {
         entry.lockedBy.forEach(dependencyName -> {
            TalentGroup<?> dependency = ModConfigs.TALENTS.getByName(dependencyName);
            talents.add(dependency);
         });
         return talents;
      }
   }

   public List<TalentGroup<?>> getTalentsDependingOn(String talentName) {
      List<TalentGroup<?>> talents = new LinkedList<>();
      TalentGroup<?> talent = ModConfigs.TALENTS.getByName(talentName);

      for (TalentGroup<?> otherTalent : ModConfigs.TALENTS.getAll()) {
         List<TalentGroup<?>> dependencies = ModConfigs.SKILL_GATES.getGates().getDependencyTalents(otherTalent.getParentName());
         if (dependencies.contains(talent)) {
            talents.add(otherTalent);
         }
      }

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

   public boolean isLocked(TalentGroup<?> talent, TalentTree talentTree) {
      SkillGates gates = ModConfigs.SKILL_GATES.getGates();

      for (TalentGroup<?> dependencyTalent : gates.getDependencyTalents(talent.getParentName())) {
         if (!talentTree.getNodeOf(dependencyTalent).isLearned()) {
            return true;
         }
      }

      for (TalentGroup<?> lockedByTalent : gates.getLockedByTalents(talent.getParentName())) {
         if (talentTree.getNodeOf(lockedByTalent).isLearned()) {
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
