package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.VeinMinerFortuneConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.util.OverlevelEnchantHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class VeinMinerFortuneAbility extends VeinMinerAbility<VeinMinerFortuneConfig> {
   protected ItemStack getVeinMiningItem(PlayerEntity player, VeinMinerFortuneConfig config) {
      ItemStack stack = super.getVeinMiningItem(player, config).func_77946_l();
      return OverlevelEnchantHelper.increaseFortuneBy(stack, config.getAdditionalFortuneLevel());
   }
}
