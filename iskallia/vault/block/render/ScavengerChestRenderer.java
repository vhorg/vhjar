package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.ScavengerChestBlock;
import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.block.model.ScavengerChestModel;
import iskallia.vault.init.ModBlocks;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ScavengerChestRenderer implements BlockEntityRenderer<ScavengerChestTileEntity> {
   public static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/scavenger_chest"));
   private static final ScavengerChestModel CHEST_MODEL = new ScavengerChestModel();

   public ScavengerChestRenderer(Context context) {
   }

   public void render(
      ScavengerChestTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      boolean isInWorldRender = tileEntity.hasLevel();
      BlockState renderState = isInWorldRender
         ? tileEntity.getBlockState()
         : (BlockState)ModBlocks.SCAVENGER_CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      float hAngle = ((Direction)renderState.getValue(ChestBlock.FACING)).toYRot();
      NeighborCombineResult<? extends ChestBlockEntity> lidCallback = Combiner::acceptNone;
      float lidRotation = ((Float2FloatFunction)lidCallback.apply(ScavengerChestBlock.opennessCombiner(tileEntity))).get(partialTicks);
      lidRotation = 1.0F - lidRotation;
      lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
      CHEST_MODEL.setLidAngle(lidRotation);
      int combinedLidLight = ((Int2IntFunction)lidCallback.apply(new BrightnessCombiner())).applyAsInt(combinedLight);
      VertexConsumer vb = MATERIAL.buffer(buffer, RenderType::entityCutout);
      matrixStack.pushPose();
      matrixStack.translate(0.5, 0.5, 0.5);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(-hAngle));
      CHEST_MODEL.renderToBuffer(matrixStack, vb, combinedLidLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
   }
}
