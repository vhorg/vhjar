package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.ScavengerChestBlock;
import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.block.model.ScavengerChestModel;
import iskallia.vault.init.ModBlocks;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityMerger.ICallback;
import net.minecraft.tileentity.TileEntityMerger.ICallbackWrapper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class ScavengerChestRenderer extends TileEntityRenderer<ScavengerChestTileEntity> {
   public static final RenderMaterial MATERIAL = new RenderMaterial(Atlases.field_228747_f_, Vault.id("entity/chest/scavenger_chest"));
   private static final ScavengerChestModel CHEST_MODEL = new ScavengerChestModel();

   public ScavengerChestRenderer(TileEntityRendererDispatcher terd) {
      super(terd);
   }

   public void render(
      ScavengerChestTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      boolean isInWorldRender = tileEntity.func_145830_o();
      BlockState renderState = isInWorldRender
         ? tileEntity.func_195044_w()
         : (BlockState)ModBlocks.SCAVENGER_CHEST.func_176223_P().func_206870_a(ChestBlock.field_176459_a, Direction.SOUTH);
      float hAngle = ((Direction)renderState.func_177229_b(ChestBlock.field_176459_a)).func_185119_l();
      ICallbackWrapper<? extends ChestTileEntity> lidCallback = ICallback::func_225537_b_;
      float lidRotation = ((Float2FloatFunction)lidCallback.apply(ScavengerChestBlock.func_226917_a_(tileEntity))).get(partialTicks);
      lidRotation = 1.0F - lidRotation;
      lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
      CHEST_MODEL.setLidAngle(lidRotation);
      int combinedLidLight = ((Int2IntFunction)lidCallback.apply(new DualBrightnessCallback())).applyAsInt(combinedLight);
      IVertexBuilder vb = MATERIAL.func_229311_a_(buffer, RenderType::func_228638_b_);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 0.5, 0.5);
      matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-hAngle));
      CHEST_MODEL.func_225598_a_(matrixStack, vb, combinedLidLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.func_227865_b_();
   }
}
