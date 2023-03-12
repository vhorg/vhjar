package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.gear.attribute.ability.special.MegaJumpVelocityModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MegaJumpBreakDownAbility extends MegaJumpAbility<MegaJumpBreakDownConfig> {
   protected boolean canDoAction(MegaJumpBreakDownConfig config, ServerPlayer player, boolean active) {
      return super.canDoAction(config, player, active) && ServerVaults.isInVault(player);
   }

   protected AbilityActionResult doAction(MegaJumpBreakDownConfig config, ServerPlayer player, boolean active) {
      int height = config.getHeight();

      for (ConfiguredModification<IntValueConfig, MegaJumpVelocityModification> mod : SpecialAbilityModification.getModifications(
         player, MegaJumpVelocityModification.class
      )) {
         height = mod.modification().adjustHeightConfig(mod.config(), height);
      }

      if (height == 0) {
         this.breakBlocks(height, player);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      } else {
         double magnitude = height * 0.15;
         double addY = -Math.min(0.0, player.getDeltaMovement().y());
         player.push(0.0, -(addY + magnitude), 0.0);
         player.startFallFlying();
         player.hurtMarked = true;
         this.breakBlocks(height, player);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   private void breakBlocks(int height, ServerPlayer player) {
      ServerLevel sWorld = (ServerLevel)player.getCommandSenderWorld();
      AbilityTree abilityTree = PlayerAbilitiesData.get(sWorld).getAbilities(player);
      AbilityNode<?, ?> focusedAbilityNode = abilityTree.getSelectedAbility();
      if (focusedAbilityNode != null && focusedAbilityNode.getAbility() == this) {
         BlockHelper.withEllipsoidPositions(player.blockPosition().below(3), 4.0F, Math.max(height, 5), 4.0F, offset -> {
            BlockState state = sWorld.getBlockState(offset);
            if (this.canBreakBlock(state)) {
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
