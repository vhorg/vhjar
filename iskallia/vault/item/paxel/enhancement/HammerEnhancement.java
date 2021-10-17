package iskallia.vault.item.paxel.enhancement;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.util.BlockDropCaptureHelper;
import iskallia.vault.util.BlockHelper;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class HammerEnhancement extends PaxelEnhancement {
   @Override
   public Color getColor() {
      return Color.func_240743_a_(-10042064);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onBlockMined(BreakEvent event) {
      ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
      ItemStack heldStack = player.func_184614_ca();
      if (PaxelEnhancements.getEnhancement(heldStack) instanceof HammerEnhancement) {
         ServerWorld world = (ServerWorld)event.getWorld();
         BlockPos centerPos = event.getPos();
         Axis axis = calcBreakAxis(player, centerPos);
         List<BlockPos> sidePoses = breakPoses(centerPos, axis);
         BlockState centerState = world.func_180495_p(centerPos);
         float centerHardness = centerState.func_185887_b(world, centerPos);
         ActiveFlags.IS_AOE_MINING
            .runIfNotSet(
               () -> {
                  for (BlockPos sidePos : sidePoses) {
                     BlockState state = world.func_180495_p(sidePos);
                     if (!state.func_177230_c().isAir(state, world, sidePos)) {
                        float sideHardness = state.func_185887_b(world, sidePos);
                        if (!(sideHardness > centerHardness) && sideHardness != -1.0F) {
                           BlockDropCaptureHelper.startCapturing();

                           try {
                              BlockHelper.breakBlock(world, player, sidePos, true, true);
                              BlockHelper.damageMiningItem(heldStack, player, 1);
                           } finally {
                              BlockDropCaptureHelper.getCapturedStacksAndStop()
                                 .forEach(entity -> Block.func_180635_a(world, entity.func_233580_cy_(), entity.func_92059_d()));
                           }
                        }
                     }
                  }
               }
            );
      }
   }

   public static Axis calcBreakAxis(ServerPlayerEntity player, BlockPos blockPos) {
      Vector3d eyePosition = player.func_174824_e(1.0F);
      Vector3d look = player.func_70676_i(1.0F);
      Vector3d endPos = eyePosition.func_72441_c(look.field_72450_a * 5.0, look.field_72448_b * 5.0, look.field_72449_c * 5.0);
      RayTraceContext rayTraceContext = new RayTraceContext(player.func_174824_e(1.0F), endPos, BlockMode.OUTLINE, FluidMode.NONE, player);
      BlockRayTraceResult result = player.field_70170_p.func_217299_a(rayTraceContext);
      return result.func_216354_b().func_176740_k();
   }

   public static List<BlockPos> breakPoses(BlockPos blockPos, Axis axis) {
      List<BlockPos> poses = new LinkedList<>();
      if (axis == Axis.Y) {
         poses.add(blockPos.func_177976_e());
         poses.add(blockPos.func_177976_e().func_177978_c());
         poses.add(blockPos.func_177976_e().func_177968_d());
         poses.add(blockPos.func_177974_f());
         poses.add(blockPos.func_177974_f().func_177978_c());
         poses.add(blockPos.func_177974_f().func_177968_d());
         poses.add(blockPos.func_177978_c());
         poses.add(blockPos.func_177968_d());
      } else if (axis == Axis.X) {
         poses.add(blockPos.func_177984_a());
         poses.add(blockPos.func_177984_a().func_177978_c());
         poses.add(blockPos.func_177984_a().func_177968_d());
         poses.add(blockPos.func_177977_b());
         poses.add(blockPos.func_177977_b().func_177978_c());
         poses.add(blockPos.func_177977_b().func_177968_d());
         poses.add(blockPos.func_177978_c());
         poses.add(blockPos.func_177968_d());
      } else if (axis == Axis.Z) {
         poses.add(blockPos.func_177984_a());
         poses.add(blockPos.func_177984_a().func_177976_e());
         poses.add(blockPos.func_177984_a().func_177974_f());
         poses.add(blockPos.func_177977_b());
         poses.add(blockPos.func_177977_b().func_177976_e());
         poses.add(blockPos.func_177977_b().func_177974_f());
         poses.add(blockPos.func_177976_e());
         poses.add(blockPos.func_177974_f());
      }

      return poses;
   }
}
