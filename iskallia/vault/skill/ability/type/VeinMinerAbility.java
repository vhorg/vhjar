package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VeinMinerAbility extends PlayerAbility {
   @Expose
   private final int blockLimit;

   public VeinMinerAbility(int cost, int blockLimit) {
      super(cost, PlayerAbility.Behavior.HOLD_TO_ACTIVATE);
      this.blockLimit = blockLimit;
   }

   public int getBlockLimit() {
      return this.blockLimit;
   }

   @SubscribeEvent
   public static void onBlockMined(BreakEvent event) {
      if (!event.getWorld().func_201670_d()) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
         AbilityTree abilityTree = PlayerAbilitiesData.get((ServerWorld)event.getWorld()).getAbilities(player);
         if (!abilityTree.isActive()) {
            return;
         }

         AbilityNode<?> focusedAbilityNode = abilityTree.getFocusedAbility();
         if (focusedAbilityNode != null) {
            PlayerAbility focusedAbility = focusedAbilityNode.getAbility();
            if (focusedAbility instanceof VeinMinerAbility) {
               VeinMinerAbility veinMinerAbility = (VeinMinerAbility)focusedAbility;
               ServerWorld world = (ServerWorld)event.getWorld();
               BlockPos pos = event.getPos();
               BlockState blockState = world.func_180495_p(pos);
               if (floodMine(player, world, blockState.func_177230_c(), pos, veinMinerAbility.getBlockLimit())) {
                  event.setCanceled(true);
               }

               abilityTree.setSwappingLocked(true);
            }
         }
      }
   }

   public static boolean floodMine(ServerPlayerEntity player, ServerWorld world, Block targetBlock, BlockPos pos, int limit) {
      if (world.func_180495_p(pos).func_177230_c() != targetBlock) {
         return false;
      } else {
         ItemStack heldItem = player.func_184586_b(player.func_184600_cs());
         if (heldItem.func_77984_f()) {
            int usesLeft = heldItem.func_77958_k() - heldItem.func_77952_i();
            if (usesLeft <= 1) {
               return false;
            }
         }

         int traversedBlocks = 0;
         List<ItemStack> itemDrops = new LinkedList<>();
         Queue<BlockPos> positionQueue = new LinkedList<>();
         itemDrops.addAll(destroyBlockAs(world, pos, player));
         positionQueue.add(pos);
         traversedBlocks++;

         label64:
         while (!positionQueue.isEmpty()) {
            BlockPos headPos = positionQueue.poll();

            for (int x = -1; x <= 1; x++) {
               for (int y = -1; y <= 1; y++) {
                  for (int z = -1; z <= 1; z++) {
                     if (x != 0 || y != 0 || z != 0) {
                        if (traversedBlocks >= limit) {
                           break label64;
                        }

                        BlockPos curPos = headPos.func_177982_a(x, y, z);
                        if (world.func_180495_p(curPos).func_177230_c() == targetBlock) {
                           itemDrops.addAll(destroyBlockAs(world, curPos, player));
                           positionQueue.add(curPos);
                           traversedBlocks++;
                        }
                     }
                  }
               }
            }
         }

         itemDrops.forEach(stack -> Block.func_180635_a(world, pos, stack));
         return true;
      }
   }

   public static List<ItemStack> destroyBlockAs(ServerWorld world, BlockPos pos, PlayerEntity player) {
      ItemStack heldItem = player.func_184586_b(player.func_184600_cs());
      if (heldItem.func_77984_f()) {
         int usesLeft = heldItem.func_77958_k() - heldItem.func_77952_i();
         if (usesLeft <= 1) {
            return Collections.emptyList();
         }

         heldItem.func_222118_a(1, player, playerEntity -> {});
      }

      List<ItemStack> drops = Block.func_220077_a(world.func_180495_p(pos), world, pos, world.func_175625_s(pos), player, heldItem);
      world.func_225521_a_(pos, false, player);
      return drops;
   }
}
