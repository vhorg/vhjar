package iskallia.vault.world.vault.logic.objective.architect.modifier;

import iskallia.vault.init.ModConfigs;

public class RandomVoteModifier extends VoteModifier {
   public RandomVoteModifier(String name, String description, int voteLockDurationChangeSeconds) {
      super(name, description, voteLockDurationChangeSeconds);
   }

   public VoteModifier rollModifier() {
      VoteModifier random = null;

      while (random == null || random instanceof RandomVoteModifier) {
         random = ModConfigs.ARCHITECT_EVENT.generateRandomModifier();
      }

      return random;
   }
}
