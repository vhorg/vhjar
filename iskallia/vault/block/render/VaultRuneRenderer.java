package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.block.entity.VaultRuneTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

public class VaultRuneRenderer extends TileEntityRenderer<VaultRuneTileEntity> {
   private Minecraft mc = Minecraft.func_71410_x();

   public VaultRuneRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      VaultRuneTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      ClientPlayerEntity player = this.mc.field_71439_g;
      Vector3d eyePosition = player.func_174824_e(1.0F);
      Vector3d look = player.func_70676_i(1.0F);
      Vector3d endPos = eyePosition.func_72441_c(look.field_72450_a * 5.0, look.field_72448_b * 5.0, look.field_72449_c * 5.0);
      RayTraceContext context = new RayTraceContext(eyePosition, endPos, BlockMode.OUTLINE, FluidMode.NONE, player);
      BlockRayTraceResult result = player.field_70170_p.func_217299_a(context);
      if (result.func_216350_a().equals(tileEntity.func_174877_v())) {
         StringTextComponent text = new StringTextComponent(tileEntity.getBelongsTo());
         this.renderLabel(matrixStack, buffer, combinedLight, text, -1);
      }
   }

   private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, StringTextComponent text, int color) {
      FontRenderer fontRenderer = this.mc.field_71466_p;
      matrixStack.func_227860_a_();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.func_238414_a_(text) / 2;
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      matrixStack.func_227861_a_(0.5, 1.4F, 0.5);
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(this.mc.func_175598_ae().func_229098_b_());
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      fontRenderer.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.func_227865_b_();
   }
}
