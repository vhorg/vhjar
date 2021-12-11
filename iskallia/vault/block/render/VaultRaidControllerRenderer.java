package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;

public class VaultRaidControllerRenderer extends TileEntityRenderer<VaultRaidControllerTileEntity> {
   public VaultRaidControllerRenderer(TileEntityRendererDispatcher dispatcher) {
      super(dispatcher);
   }

   public void render(
      VaultRaidControllerTileEntity te, float partialTicks, MatrixStack renderStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      if (!te.didTriggerRaid()) {
         this.drawHoveringModifiers(te.getModifierDisplay(), partialTicks, renderStack, buffer, combinedLight);
      }
   }

   private void drawHoveringModifiers(List<ITextComponent> modifiers, float pTicks, MatrixStack renderStack, IRenderTypeBuffer buffer, int combinedLight) {
      EntityRendererManager mgr = Minecraft.func_71410_x().func_175598_ae();
      FontRenderer fr = mgr.func_78716_a();
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(0.5, 2.5, 0.5);
      renderStack.func_227863_a_(mgr.func_229098_b_());
      renderStack.func_227862_a_(-0.025F, -0.025F, 0.025F);
      Matrix4f matr = renderStack.func_227866_c_().func_227870_a_();
      float textBgOpacity = Minecraft.func_71410_x().field_71474_y.func_216840_a(0.25F);
      int textBgAlpha = (int)(textBgOpacity * 255.0F) << 24;

      for (ITextComponent modifier : modifiers) {
         float xShift = fr.func_238414_a_(modifier) / 2.0F;
         fr.func_243247_a(modifier, -xShift, 0.0F, 553648127, false, matr, buffer, true, textBgAlpha, combinedLight);
         fr.func_243247_a(modifier, -xShift, 0.0F, -1, false, matr, buffer, false, 0, combinedLight);
         renderStack.func_227861_a_(0.0, -10.0, 0.0);
      }

      renderStack.func_227865_b_();
   }

   private boolean isInDrawDistance(BlockPos pos) {
      EntityRendererManager mgr = Minecraft.func_71410_x().func_175598_ae();
      return mgr.func_78714_a(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()) < 4096.0;
   }
}
