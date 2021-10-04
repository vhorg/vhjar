package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.VeinMinerDurabilityConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class VeinMinerDurabilityAbility extends VeinMinerAbility<VeinMinerDurabilityConfig> {
   public void damageMiningItem(ItemStack heldItem, PlayerEntity player, VeinMinerDurabilityConfig config) {
      if (!(rand.nextFloat() >= config.getNoDurabilityUsageChance())) {
         super.damageMiningItem(heldItem, player, config);
      }
   }
}
