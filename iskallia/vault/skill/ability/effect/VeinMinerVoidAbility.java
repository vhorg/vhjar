package iskallia.vault.skill.ability.effect;

import iskallia.vault.tags.ModBlockTags;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class VeinMinerVoidAbility extends VeinMinerAbility {
   public VeinMinerVoidAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, int blockLimit) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, blockLimit);
   }

   public VeinMinerVoidAbility() {
   }

   @Override
   public boolean shouldVoid(ServerLevel level, ServerPlayer player, BlockState target) {
      return ServerVaults.get(player.level).isPresent() && !target.is(ModBlockTags.VOIDMINE_EXCLUSIONS);
   }
}
