package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.ExecuteBuffConfig;
import iskallia.vault.skill.ability.effect.ExecuteAbility;

public class ExecuteBuffAbility extends ExecuteAbility<ExecuteBuffConfig> {
   protected boolean removeEffect(ExecuteBuffConfig cfg) {
      return rand.nextFloat() < cfg.getRegainBuffChance();
   }
}
