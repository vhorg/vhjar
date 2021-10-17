package iskallia.vault.skill.ability.effect;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.VeinMinerConfig;
import iskallia.vault.util.BlockDropCaptureHelper;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VeinMinerAbility<C extends VeinMinerConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Vein Miner";
   }

   @SubscribeEvent
   public void onBlockMined(BreakEvent event) {
      if (!event.getWorld().func_201670_d() && !(event.getPlayer() instanceof FakePlayer)) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
         AbilityTree abilityTree = PlayerAbilitiesData.get((ServerWorld)event.getWorld()).getAbilities(player);
         if (abilityTree.isActive()) {
            ActiveFlags.IS_AOE_MINING.runIfNotSet(() -> {
               AbilityNode<?, ?> focusedAbilityNode = abilityTree.getSelectedAbility();
               if (focusedAbilityNode != null && focusedAbilityNode.getAbility() == this) {
                  C veinMinerConfig = (C)focusedAbilityNode.getAbilityConfig();
                  ServerWorld world = (ServerWorld)event.getWorld();
                  BlockPos pos = event.getPos();
                  BlockState blockState = world.func_180495_p(pos);
                  if (this.captureVeinMining(player, world, blockState.func_177230_c(), pos, veinMinerConfig)) {
                     event.setCanceled(true);
                  }

                  abilityTree.setSwappingLocked(true);
               }
            });
         }
      }
   }

   protected boolean captureVeinMining(ServerPlayerEntity player, ServerWorld world, Block targetBlock, BlockPos pos, C config) {
      BlockDropCaptureHelper.startCapturing();

      boolean var6;
      try {
         var6 = this.doVeinMine(player, world, targetBlock, pos, config);
      } finally {
         BlockDropCaptureHelper.getCapturedStacksAndStop().forEach(entity -> Block.func_180635_a(world, entity.func_233580_cy_(), entity.func_92059_d()));
      }

      return var6;
   }

   protected boolean doVeinMine(ServerPlayerEntity player, ServerWorld world, Block targetBlock, BlockPos pos, C config) {
      ItemStack heldItem = player.func_184586_b(Hand.MAIN_HAND);
      if (heldItem.func_77984_f()) {
         int usesLeft = heldItem.func_77958_k() - heldItem.func_77952_i();
         if (usesLeft <= 1) {
            return false;
         }
      }

      int limit = config.getBlockLimit();
      Set<BlockPos> traversedBlocks = new HashSet<>();
      Queue<BlockPos> positionQueue = new LinkedList<>();
      positionQueue.add(pos);

      while (!positionQueue.isEmpty()) {
         BlockPos headPos = positionQueue.poll();
         BlockPos.func_239588_b_(headPos, 1, 1, 1).forEach(offset -> {
            if (traversedBlocks.size() >= limit) {
               positionQueue.clear();
            } else if (!traversedBlocks.contains(offset)) {
               BlockState at = world.func_180495_p(offset);
               if (!at.isAir(world, offset) && at.func_177230_c() == targetBlock) {
                  if (this.shouldVoid(world, targetBlock)) {
                     at.removedByPlayer(world, offset, player, false, at.func_204520_s());
                     BlockHelper.damageMiningItem(heldItem, player, 1);
                  } else if (this.doDestroy(world, offset, player, config)) {
                     BlockHelper.damageMiningItem(heldItem, player, 1);
                  }

                  positionQueue.add(offset.func_185334_h());
                  traversedBlocks.add(offset.func_185334_h());
               }
            }
         });
      }

      return true;
   }

   private boolean doDestroy(ServerWorld world, BlockPos pos, ServerPlayerEntity player, C config) {
      ItemStack miningStack = this.getVeinMiningItem(player, config);
      return BlockHelper.breakBlock(world, player, pos, world.func_180495_p(pos), miningStack, true, false);
   }

   public void damageMiningItem(ItemStack heldItem, PlayerEntity player, C config) {
      heldItem.func_222118_a(1, player, playerEntity -> {});
   }

   protected ItemStack getVeinMiningItem(PlayerEntity player, C config) {
      return player.func_184586_b(Hand.MAIN_HAND);
   }

   public boolean shouldVoid(ServerWorld world, Block targetBlock) {
      return false;
   }
}
