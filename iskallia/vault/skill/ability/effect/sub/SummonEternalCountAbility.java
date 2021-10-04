package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.SummonEternalCountConfig;
import iskallia.vault.skill.ability.effect.SummonEternalAbility;
import iskallia.vault.world.data.EternalsData;

public class SummonEternalCountAbility extends SummonEternalAbility<SummonEternalCountConfig> {
   protected int getEternalCount(EternalsData.EternalGroup eternals, SummonEternalCountConfig config) {
      return super.getEternalCount(eternals, config) + config.getAdditionalCount();
   }
}
