package iskallia.vault.skill.ability.effect;

import iskallia.vault.skill.ability.effect.spi.AbstractVeinMinerAbility;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class VeinMinerAbility extends AbstractVeinMinerAbility {
   public VeinMinerAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, int blockLimit) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, blockLimit);
   }

   public VeinMinerAbility() {
   }

   @Override
   protected ItemStack getVeinMiningItemProxy(Player player) {
      return player.getItemInHand(InteractionHand.MAIN_HAND).copy();
   }

   @Override
   public boolean shouldVoid(ServerLevel level, ServerPlayer player, BlockState target) {
      return false;
   }
}
