package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.VeinMinerDurabilityConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.util.OverlevelEnchantHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VeinMinerDurabilityAbility extends VeinMinerAbility<VeinMinerDurabilityConfig> {
   protected ItemStack getVeinMiningItemProxy(Player player, VeinMinerDurabilityConfig config) {
      ItemStack itemStackCopy = super.getVeinMiningItemProxy(player, config).copy();
      return OverlevelEnchantHelper.increaseUnbreakingBy(itemStackCopy, config.getAdditionalUnbreakingLevel());
   }
}
