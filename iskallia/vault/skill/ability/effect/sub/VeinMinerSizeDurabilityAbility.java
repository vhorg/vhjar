package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.VeinMinerSizeDurabilityConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class VeinMinerSizeDurabilityAbility extends VeinMinerAbility<VeinMinerSizeDurabilityConfig> {
   public void damageMiningItem(ItemStack heldItem, PlayerEntity player, VeinMinerSizeDurabilityConfig config) {
      super.damageMiningItem(heldItem, player, config);
      if (rand.nextFloat() < config.getDoubleDurabilityCostChance()) {
         super.damageMiningItem(heldItem, player, config);
      }
   }
}
