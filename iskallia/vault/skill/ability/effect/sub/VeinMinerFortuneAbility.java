package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.VeinMinerFortuneConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.util.OverlevelEnchantHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VeinMinerFortuneAbility extends VeinMinerAbility<VeinMinerFortuneConfig> {
   protected ItemStack getVeinMiningItemProxy(Player player, VeinMinerFortuneConfig config) {
      ItemStack stack = super.getVeinMiningItemProxy(player, config).copy();
      return OverlevelEnchantHelper.increaseFortuneBy(stack, config.getAdditionalFortuneLevel());
   }
}
