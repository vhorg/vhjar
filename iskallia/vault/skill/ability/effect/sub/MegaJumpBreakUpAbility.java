package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakUpConfig;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.ServerVaults;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MegaJumpBreakUpAbility extends MegaJumpAbility<MegaJumpBreakUpConfig> {
   private final Map<UUID, Integer> playerBreakMap = new HashMap<>();

   protected boolean canDoAction(MegaJumpBreakUpConfig config, ServerPlayer player, boolean active) {
      return super.canDoAction(config, player, active) && ServerVaults.isInVault(player);
   }

   protected AbilityActionResult doAction(MegaJumpBreakUpConfig config, ServerPlayer player, boolean active) {
      super.doAction(config, player, active);
      this.playerBreakMap.put(player.getUUID(), 30);
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.START && !event.player.getCommandSenderWorld().isClientSide() && event.player.getCommandSenderWorld() instanceof ServerLevel) {
         Player player = event.player;
         UUID plUUID = player.getUUID();
         if (this.playerBreakMap.containsKey(plUUID)) {
            int ticks = this.playerBreakMap.get(plUUID);
            if (--ticks <= 0) {
               this.playerBreakMap.remove(plUUID);
            } else {
               this.playerBreakMap.put(plUUID, ticks);
               ServerLevel sWorld = (ServerLevel)player.getCommandSenderWorld();
               AbilityTree abilityTree = PlayerAbilitiesData.get(sWorld).getAbilities(player);
               AbilityNode<?, ?> focusedAbilityNode = abilityTree.getSelectedAbility();
               if (focusedAbilityNode != null && focusedAbilityNode.getAbility() == this) {
                  for (BlockPos offset : BlockHelper.getOvalPositions(player.blockPosition().above(3), 4.0F, 6.0F)) {
                     BlockState state = sWorld.getBlockState(offset);
                     if (!state.isAir() && (!state.requiresCorrectToolForDrops() || TierSortingRegistry.isCorrectTierForDrops(Tiers.IRON, state))) {
                        float hardness = state.getDestroySpeed(sWorld, offset);
                        if (hardness >= 0.0F && hardness <= 25.0F) {
                           this.destroyBlock(sWorld, offset, player);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void destroyBlock(ServerLevel world, BlockPos pos, Player player) {
      ItemStack miningItem = new ItemStack(Items.DIAMOND_PICKAXE);
      Block.dropResources(world.getBlockState(pos), world, pos, world.getBlockEntity(pos), null, miningItem);
      world.destroyBlock(pos, false, player);
   }
}
