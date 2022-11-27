package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.VeinMinerVoidConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.tags.ModBlockTags;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class VeinMinerVoidAbility extends VeinMinerAbility<VeinMinerVoidConfig> {
   @Override
   public boolean shouldVoid(ServerLevel level, ServerPlayer player, BlockState target) {
      return ServerVaults.isInVault(player) && !target.is(ModBlockTags.VOIDMINE_EXCLUSIONS);
   }
}
