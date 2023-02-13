package iskallia.vault.item.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import iskallia.vault.event.ActiveFlags;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent.HighlightBlock;

@Deprecated(
   forRemoval = true
)
public class PaxelHighlightBlockEvent {
   @OnlyIn(Dist.CLIENT)
   public static void onBlockHighlight(HighlightBlock event) {
      if (event.getCamera().getEntity() instanceof Player player) {
         BlockPos var18 = event.getTarget().getBlockPos();
         Direction side = event.getTarget().getDirection();
         ItemStack stack = player.getMainHandItem();
         if (!ActiveFlags.IS_AOE_MINING.isSet()) {
            List<PaxelItem.Perk> perks = PaxelItem.getPerks(stack);
            int range;
            if (perks.contains(PaxelItem.Perk.SHATTERING)) {
               range = PaxelItem.Perk.SHATTERING.getDigRadius();
            } else if (perks.contains(PaxelItem.Perk.EXCAVATING)) {
               range = PaxelItem.Perk.EXCAVATING.getDigRadius();
            } else {
               if (!perks.contains(PaxelItem.Perk.HAMMERING)) {
                  return;
               }

               range = PaxelItem.Perk.HAMMERING.getDigRadius();
            }

            range = (range - 1) / 2;
            Level level = player.level;
            if (level.isAreaLoaded(var18, range)) {
               Direction ort1 = side != Direction.UP && side != Direction.DOWN ? side.getCounterClockWise() : player.getDirection().getClockWise();
               Direction ort2 = side != Direction.UP && side != Direction.DOWN
                  ? Direction.UP
                  : (side == Direction.UP ? player.getDirection() : player.getDirection().getOpposite());
               Vec2 offset = PaxelItem.getOffset(stack);
               var18 = var18.relative(ort1, (int)offset.x);
               var18 = var18.relative(ort2, (int)offset.y);
               Iterator<MutableBlockPos> it = BlockPos.spiralAround(var18, range, ort1, ort2).iterator();

               while (it.hasNext() && !stack.isEmpty()) {
                  MultiBufferSource multiBufferSource = event.getMultiBufferSource();
                  PoseStack poseStack = event.getPoseStack();
                  Camera camera = event.getCamera();
                  BlockPos next = it.next().immutable();
                  BlockState state = level.getBlockState(next);
                  if (!state.isAir() && level.getWorldBorder().isWithinBounds(next)) {
                     VertexConsumer vertexconsumer2 = multiBufferSource.getBuffer(RenderType.lines());
                     renderHitOutline(
                        poseStack,
                        vertexconsumer2,
                        level,
                        camera.getEntity(),
                        camera.getPosition().x(),
                        camera.getPosition().y(),
                        camera.getPosition().z(),
                        next,
                        state
                     );
                  }
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderHitOutline(
      PoseStack pPoseStack, VertexConsumer pConsumer, Level level, Entity pEntity, double pCamX, double pCamY, double pCamZ, BlockPos pPos, BlockState pState
   ) {
      renderShape(
         pPoseStack,
         pConsumer,
         pState.getShape(level, pPos, CollisionContext.of(pEntity)),
         pPos.getX() - pCamX,
         pPos.getY() - pCamY,
         pPos.getZ() - pCamZ,
         0.0F,
         1.0F,
         0.0F,
         0.7F
      );
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderShape(
      PoseStack pPoseStack, VertexConsumer pConsumer, VoxelShape pShape, double pX, double pY, double pZ, float pRed, float pGreen, float pBlue, float pAlpha
   ) {
      Pose posestack$pose = pPoseStack.last();
      pShape.forAllEdges(
         (p_194324_, p_194325_, p_194326_, p_194327_, p_194328_, p_194329_) -> {
            float f = (float)(p_194327_ - p_194324_);
            float f1 = (float)(p_194328_ - p_194325_);
            float f2 = (float)(p_194329_ - p_194326_);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            pConsumer.vertex(posestack$pose.pose(), (float)(p_194324_ + pX), (float)(p_194325_ + pY), (float)(p_194326_ + pZ))
               .color(pRed, pGreen, pBlue, pAlpha)
               .normal(posestack$pose.normal(), f, f1, f2)
               .endVertex();
            pConsumer.vertex(posestack$pose.pose(), (float)(p_194327_ + pX), (float)(p_194328_ + pY), (float)(p_194329_ + pZ))
               .color(pRed, pGreen, pBlue, pAlpha)
               .normal(posestack$pose.normal(), f, f1, f2)
               .endVertex();
         }
      );
   }
}
