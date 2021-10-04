package iskallia.vault.skill.talent.type.archetype;

import iskallia.vault.skill.talent.type.PlayerTalent;

public abstract class ArchetypeTalent extends PlayerTalent {
   public ArchetypeTalent(int cost) {
      super(cost);
   }

   public ArchetypeTalent(int cost, int levelRequirement) {
      super(cost, levelRequirement);
   }
}
