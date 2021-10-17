package iskallia.vault.util;

import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.VeinMinerConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class BlockHelper {
   public static Iterable<BlockPos> getSphericalPositions(BlockPos center, float radius) {
      return getOvalPositions(center, radius, radius);
   }

   public static Iterable<BlockPos> getOvalPositions(BlockPos center, float widthRadius, float heightRadius) {
      Collection<BlockPos> positions = new Stack<>();
      int wRadius = MathHelper.func_76123_f(widthRadius);
      int hRadius = MathHelper.func_76123_f(heightRadius);
      BlockPos pos = BlockPos.field_177992_a;

      for (int xx = -wRadius; xx <= wRadius; xx++) {
         for (int yy = -hRadius; yy <= hRadius; yy++) {
            for (int zz = -wRadius; zz <= wRadius; zz++) {
               if (pos.func_218140_a(xx + 0.5F, yy + 0.5F, zz + 0.5F, true) <= Math.max(widthRadius, heightRadius)) {
                  positions.add(pos.func_177971_a(center).func_177982_a(xx, yy, zz));
               }
            }
         }
      }

      return positions;
   }

   public static void damageMiningItem(ItemStack stack, ServerPlayerEntity player, int amount) {
      Runnable damageItem = () -> stack.func_222118_a(amount, player, playerEntity -> {});
      AbilityTree abilityTree = PlayerAbilitiesData.get(player.func_71121_q()).getAbilities(player);
      if (abilityTree.isActive()) {
         AbilityNode<?, ?> focusedAbilityNode = abilityTree.getSelectedAbility();
         if (focusedAbilityNode != null && focusedAbilityNode.getAbility() instanceof VeinMinerAbility) {
            AbilityConfig cfg = focusedAbilityNode.getAbilityConfig();
            if (cfg instanceof VeinMinerConfig) {
               damageItem = () -> ((VeinMinerAbility)focusedAbilityNode.getAbility()).damageMiningItem(stack, player, (VeinMinerConfig)cfg);
            }
         }
      }

      for (int i = 0; i < amount; i++) {
         damageItem.run();
      }
   }

   public static boolean breakBlock(ServerWorld world, ServerPlayerEntity player, BlockPos pos, boolean breakBlock, boolean ignoreHarvestRestrictions) {
      return breakBlock(world, player, pos, world.func_180495_p(pos), player.func_184614_ca(), breakBlock, ignoreHarvestRestrictions);
   }

   public static boolean breakBlock(
      ServerWorld world,
      ServerPlayerEntity player,
      BlockPos pos,
      BlockState stateBroken,
      ItemStack heldItem,
      boolean breakBlock,
      boolean ignoreHarvestRestrictions
   ) {
      ItemStack original = player.func_184586_b(Hand.MAIN_HAND);

      boolean var8;
      try {
         player.func_184611_a(Hand.MAIN_HAND, heldItem);
         var8 = doNativeBreakBlock(world, player, pos, stateBroken, heldItem, breakBlock, ignoreHarvestRestrictions);
      } finally {
         player.func_184611_a(Hand.MAIN_HAND, original);
      }

      return var8;
   }

   private static boolean doNativeBreakBlock(
      ServerWorld world,
      ServerPlayerEntity player,
      BlockPos pos,
      BlockState stateBroken,
      ItemStack heldItem,
      boolean breakBlock,
      boolean ignoreHarvestRestrictions
   ) {
      int xp;
      try {
         boolean preCancelEvent = false;
         if (!heldItem.func_190926_b() && !heldItem.func_77973_b().func_195938_a(stateBroken, world, pos, player)) {
            preCancelEvent = true;
         }

         BreakEvent event = new BreakEvent(world, pos, stateBroken, player);
         event.setCanceled(preCancelEvent);
         MinecraftForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            return false;
         }

         xp = event.getExpToDrop();
      } catch (Exception var25) {
         return false;
      }

      if (xp == -1) {
         return false;
      } else if (heldItem.onBlockStartBreak(pos, player)) {
         return false;
      } else {
         boolean harvestable = true;

         try {
            if (!ignoreHarvestRestrictions) {
               harvestable = stateBroken.canHarvestBlock(world, pos, player);
            }
         } catch (Exception var24) {
            return false;
         }

         try {
            heldItem.func_77946_l().func_179548_a(world, stateBroken, pos, player);
         } catch (Exception var23) {
            return false;
         }

         boolean wasCapturingStates = world.captureBlockSnapshots;
         List<BlockSnapshot> previousCapturedStates = new ArrayList<>(world.capturedBlockSnapshots);
         TileEntity tileEntity = world.func_175625_s(pos);
         world.captureBlockSnapshots = true;

         try {
            if (breakBlock) {
               if (!stateBroken.removedByPlayer(world, pos, player, harvestable, Fluids.field_204541_a.func_207188_f())) {
                  restoreWorldState(world, wasCapturingStates, previousCapturedStates);
                  return false;
               }
            } else {
               stateBroken.func_177230_c().func_176208_a(world, pos, stateBroken, player);
            }
         } catch (Exception var22) {
            restoreWorldState(world, wasCapturingStates, previousCapturedStates);
            return false;
         }

         stateBroken.func_177230_c().func_176206_d(world, pos, stateBroken);
         if (harvestable) {
            try {
               stateBroken.func_177230_c().func_180657_a(world, player, pos, stateBroken, tileEntity, heldItem.func_77946_l());
            } catch (Exception var21) {
               restoreWorldState(world, wasCapturingStates, previousCapturedStates);
               return false;
            }
         }

         if (xp > 0) {
            stateBroken.func_177230_c().func_180637_b(world, pos, xp);
         }

         BlockDropCaptureHelper.startCapturing();

         try {
            world.captureBlockSnapshots = false;
            world.restoringBlockSnapshots = true;
            world.capturedBlockSnapshots.forEach(s -> {
               world.func_184138_a(s.getPos(), s.getReplacedBlock(), s.getCurrentBlock(), s.getFlag());
               s.getCurrentBlock().func_235734_a_(world, s.getPos(), 11);
            });
            world.restoringBlockSnapshots = false;
         } finally {
            BlockDropCaptureHelper.getCapturedStacksAndStop();
            world.capturedBlockSnapshots.clear();
            world.captureBlockSnapshots = wasCapturingStates;
            world.capturedBlockSnapshots.addAll(previousCapturedStates);
         }

         return true;
      }
   }

   private static void restoreWorldState(World world, boolean prevCaptureFlag, List<BlockSnapshot> prevSnapshots) {
      world.captureBlockSnapshots = false;
      world.restoringBlockSnapshots = true;
      world.capturedBlockSnapshots.forEach(s -> s.restore(true));
      world.restoringBlockSnapshots = false;
      world.capturedBlockSnapshots.clear();
      world.captureBlockSnapshots = prevCaptureFlag;
      world.capturedBlockSnapshots.addAll(prevSnapshots);
   }
}
