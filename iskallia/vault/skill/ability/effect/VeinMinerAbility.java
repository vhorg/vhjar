package iskallia.vault.skill.ability.effect;

import iskallia.vault.skill.ability.config.VeinMinerConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractVeinMinerAbility;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class VeinMinerAbility<C extends VeinMinerConfig> extends AbstractVeinMinerAbility<C> {
   @Override
   protected ItemStack getVeinMiningItemProxy(Player player, C config) {
      return player.getItemInHand(InteractionHand.MAIN_HAND).copy();
   }

   @Override
   protected boolean shouldVoid(ServerLevel level, ServerPlayer player, BlockState target) {
      return false;
   }
}
