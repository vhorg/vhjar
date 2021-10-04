package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.Vault;
import iskallia.vault.skill.ability.config.sub.VeinMinerVoidConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.server.ServerWorld;

public class VeinMinerVoidAbility extends VeinMinerAbility<VeinMinerVoidConfig> {
   @Override
   public boolean shouldVoid(ServerWorld world, Block targetBlock) {
      return world.func_234923_W_() == Vault.VAULT_KEY && !targetBlock.func_203417_a(BlockTags.func_199896_a().func_241834_b(Vault.id("voidmine_exclusions")));
   }
}
