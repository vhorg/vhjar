package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.ability.special.VeinMinerAdditionalBlocksModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import net.minecraft.world.entity.player.Player;

public class VeinMinerConfig extends AbstractAbilityConfig {
   @Expose
   private final int blockLimit;

   public VeinMinerConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, int blockLimit) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement);
      this.blockLimit = blockLimit;
   }

   public int getUnmodifiedBlockLimit() {
      return this.blockLimit;
   }

   public int getBlockLimit(Player player) {
      int blocks = this.getUnmodifiedBlockLimit();

      for (ConfiguredModification<IntValueConfig, VeinMinerAdditionalBlocksModification> mod : SpecialAbilityModification.getModifications(
         player, VeinMinerAdditionalBlocksModification.class
      )) {
         blocks = mod.modification().adjustBlockCount(mod.config(), blocks);
      }

      return blocks;
   }
}
