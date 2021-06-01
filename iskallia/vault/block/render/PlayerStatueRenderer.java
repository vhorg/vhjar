package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.entity.PlayerStatueTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;

public class PlayerStatueRenderer extends TileEntityRenderer<PlayerStatueTileEntity> {
   protected static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel(0.1F, true);
   private Minecraft mc = Minecraft.func_71410_x();

   public PlayerStatueRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      PlayerStatueTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      String latestNickname = tileEntity.getSkin().getLatestNickname();
      if (latestNickname != null && !latestNickname.equals("")) {
         ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
         RenderType renderType = PLAYER_MODEL.func_228282_a_(skinLocation);
         IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);
         BlockState blockState = tileEntity.func_195044_w();
         Direction direction = (Direction)blockState.func_177229_b(PlayerStatueBlock.FACING);
         float scale = 0.4F;
         float headScale = 1.75F;
         float crownScale = 1.5F;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.5, 0.9, 0.5);
         matrixStack.func_227862_a_(scale, scale, scale);
         matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
         PLAYER_MODEL.field_78115_e.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178722_k.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178721_j.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178724_i.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178723_h.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178730_v.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178733_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178731_d.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_178734_a.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.0, -0.62F);
         PLAYER_MODEL.field_178732_b.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227865_b_();
         matrixStack.func_227862_a_(headScale, headScale, headScale);
         PLAYER_MODEL.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227865_b_();
         Minecraft minecraft = Minecraft.func_71410_x();
         if (tileEntity.hasCrown() && minecraft.field_71439_g != null) {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0.5, 1.2, 0.5);
            matrixStack.func_227862_a_(crownScale, crownScale, crownScale);
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(minecraft.field_71439_g.field_70173_aa));
            ItemStack itemStack = new ItemStack((IItemProvider)Registry.field_212630_s.func_82594_a(Vault.id("mvp_crown")));
            IBakedModel ibakedmodel = minecraft.func_175599_af().func_184393_a(itemStack, null, null);
            minecraft.func_175599_af().func_229111_a_(itemStack, TransformType.GROUND, true, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
            matrixStack.func_227865_b_();
         }

         StringTextComponent text = new StringTextComponent(tileEntity.getSkin().getLatestNickname());
         if (this.mc.field_71476_x != null && this.mc.field_71476_x.func_216346_c() == Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult)this.mc.field_71476_x;
            if (tileEntity.func_174877_v().equals(result.func_216350_a())) {
               this.renderLabel(matrixStack, buffer, combinedLight, text, -1);
            }
         }
      }
   }

   private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, StringTextComponent text, int color) {
      FontRenderer fontRenderer = this.mc.field_71466_p;
      matrixStack.func_227860_a_();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.func_238414_a_(text) / 2;
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      matrixStack.func_227861_a_(0.5, 1.6F, 0.5);
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(this.mc.func_175598_ae().func_229098_b_());
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      fontRenderer.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.func_227865_b_();
   }
}
