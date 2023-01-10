package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakDownConfig;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

public class MegaJumpBreakDownAbility extends MegaJumpAbility<MegaJumpBreakDownConfig> {
   protected boolean canDoAction(MegaJumpBreakDownConfig config, ServerPlayer player, boolean active) {
      return super.canDoAction(config, player, active) && ServerVaults.isInVault(player);
   }

   protected AbilityActionResult doAction(MegaJumpBreakDownConfig config, ServerPlayer player, boolean active) {
      double magnitude = config.getHeight() * 0.15;
      double addY = -Math.min(0.0, player.getDeltaMovement().y());
      player.push(0.0, -(addY + magnitude), 0.0);
      player.startFallFlying();
      player.hurtMarked = true;
      this.breakBlocks(config, player);
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   private void breakBlocks(MegaJumpBreakDownConfig config, ServerPlayer player) {
      ServerLevel sWorld = (ServerLevel)player.getCommandSenderWorld();
      AbilityTree abilityTree = PlayerAbilitiesData.get(sWorld).getAbilities(player);
      AbilityNode<?, ?> focusedAbilityNode = abilityTree.getSelectedAbility();
      if (focusedAbilityNode != null && focusedAbilityNode.getAbility() == this) {
         BlockHelper.withEllipsoidPositions(player.blockPosition().below(3), 4.0F, config.getHeight(), 4.0F, offset -> {
            BlockState state = sWorld.getBlockState(offset);
            if (!state.isAir() && (!state.requiresCorrectToolForDrops() || TierSortingRegistry.isCorrectTierForDrops(Tiers.IRON, state))) {
               float hardness = state.getDestroySpeed(sWorld, offset);
               if (hardness >= 0.0F && hardness <= 25.0F) {
                  this.destroyBlock(sWorld, offset, player);
               }
            }
         });
      }
   }

   private void destroyBlock(ServerLevel world, BlockPos pos, Player player) {
      ItemStack miningItem = new ItemStack(Items.DIAMOND_PICKAXE);
      Block.dropResources(world.getBlockState(pos), world, pos, world.getBlockEntity(pos), null, miningItem);
      world.destroyBlock(pos, false, player);
   }
}
