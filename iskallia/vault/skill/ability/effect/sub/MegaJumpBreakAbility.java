package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakConfig;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MegaJumpBreakAbility extends MegaJumpAbility<MegaJumpBreakConfig> {
   private final Map<UUID, Integer> playerBreakMap = new HashMap<>();

   public boolean onAction(MegaJumpBreakConfig config, PlayerEntity player, boolean active) {
      if (super.onAction(config, player, active)) {
         this.playerBreakMap.put(player.func_110124_au(), 30);
         return true;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.START && !event.player.func_130014_f_().func_201670_d() && event.player.func_130014_f_() instanceof ServerWorld) {
         PlayerEntity player = event.player;
         UUID plUUID = player.func_110124_au();
         if (this.playerBreakMap.containsKey(plUUID)) {
            int ticks = this.playerBreakMap.get(plUUID);
            if (--ticks <= 0) {
               this.playerBreakMap.remove(plUUID);
            } else {
               this.playerBreakMap.put(plUUID, ticks);
               ServerWorld sWorld = (ServerWorld)player.func_130014_f_();
               AbilityTree abilityTree = PlayerAbilitiesData.get(sWorld).getAbilities(player);
               AbilityNode<?, ?> focusedAbilityNode = abilityTree.getSelectedAbility();
               if (focusedAbilityNode != null && focusedAbilityNode.getAbility() == this) {
                  for (BlockPos offset : BlockHelper.getOvalPositions(player.func_233580_cy_().func_177981_b(3), 4.0F, 6.0F)) {
                     BlockState state = sWorld.func_180495_p(offset);
                     if (!state.isAir(sWorld, offset) && (!state.func_235783_q_() || state.getHarvestLevel() <= 2)) {
                        float hardness = state.func_185887_b(sWorld, offset);
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

   private void destroyBlock(ServerWorld world, BlockPos pos, PlayerEntity player) {
      ItemStack miningItem = new ItemStack(Items.field_151046_w);
      Block.func_220054_a(world.func_180495_p(pos), world, pos, world.func_175625_s(pos), null, miningItem);
      world.func_225521_a_(pos, false, player);
   }
}
