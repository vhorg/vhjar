package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.core.random.RandomSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RaidActionsConfig extends Config {
   @Expose
   private Map<String, ChallengeAction<?>> values;

   @Override
   public String getName() {
      return "raid_actions";
   }

   public Optional<ChallengeAction<?>> getRandom(String id, RandomSource random) {
      return Optional.ofNullable(this.values.get(id));
   }

   @Override
   protected void reset() {
      this.values = new HashMap<>();
   }
}
