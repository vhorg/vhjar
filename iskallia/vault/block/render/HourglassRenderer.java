package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.client.util.RenderTypeDecorator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class HourglassRenderer extends TileEntityRenderer<HourglassTileEntity> {
   private static final ResourceLocation SAND_TEXTURE = Vault.id("textures/block/hourglass_sand.png");
   private static List<AxisAlignedBB> SAND_BOXES = new ArrayList<>();
   private static final int totalHeight = 28;

   public HourglassRenderer(TileEntityRendererDispatcher terd) {
      super(terd);
   }

   public void render(HourglassTileEntity te, float partialTicks, MatrixStack renderStack, IRenderTypeBuffer buffers, int combinedLight, int combinedOverlay) {
      ModelRenderer sandBoxes = this.prepareSandRender(te.getFilledPercentage());
      RenderType wrapped = RenderTypeDecorator.decorate(
         RenderType.func_228639_c_(),
         () -> Minecraft.func_71410_x().func_110434_K().func_110577_a(SAND_TEXTURE),
         () -> Minecraft.func_71410_x().func_110434_K().func_110577_a(AtlasTexture.field_110575_b)
      );
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(0.01, 0.125, 0.01);
      renderStack.func_227862_a_(0.98F, 1.0F, 0.98F);
      IVertexBuilder vb = buffers.getBuffer(wrapped);
      sandBoxes.func_228308_a_(renderStack, vb, combinedLight, combinedOverlay);
      renderStack.func_227865_b_();
      buffers.getBuffer(RenderType.func_228659_m_());
   }

   private ModelRenderer prepareSandRender(float percentage) {
      float heightPart = totalHeight * MathHelper.func_76131_a(percentage, 0.0F, 1.0F);
      ModelRenderer renderer = new ModelRenderer(16, 16, 0, 0);

      for (AxisAlignedBB box : SAND_BOXES) {
         float ySize = (float)box.func_216360_c();
         float remainingHeight = heightPart - ySize;
         if (!(remainingHeight >= 0.0F)) {
            float part = heightPart / ySize;
            renderer.func_228300_a_(
               (float)box.field_72340_a,
               (float)box.field_72338_b,
               (float)box.field_72339_c,
               (float)box.func_216364_b(),
               (float)box.func_216360_c() * part,
               (float)box.func_216362_d()
            );
            break;
         }

         renderer.func_228300_a_(
            (float)box.field_72340_a,
            (float)box.field_72338_b,
            (float)box.field_72339_c,
            (float)box.func_216364_b(),
            (float)box.func_216360_c(),
            (float)box.func_216362_d()
         );
         heightPart = (float)(heightPart - box.func_216360_c());
      }

      return renderer;
   }

   private static void shiftY(float y) {
      SAND_BOXES = SAND_BOXES.stream().map(box -> box.func_72317_d(0.0, y, 0.0)).collect(Collectors.toList());
   }

   private static AxisAlignedBB makeBox(double x, double y, double z, double width, double height, double depth) {
      return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
   }

   static {
      SAND_BOXES.add(makeBox(2.0, 0.0, 2.0, 12.0, 7.0, 12.0));
      SAND_BOXES.add(makeBox(3.0, 7.0, 3.0, 10.0, 3.0, 10.0));
      SAND_BOXES.add(makeBox(4.0, 10.0, 4.0, 8.0, 2.0, 8.0));
      SAND_BOXES.add(makeBox(5.0, 12.0, 5.0, 6.0, 1.0, 6.0));
      SAND_BOXES.add(makeBox(6.0, 13.0, 6.0, 4.0, 1.0, 4.0));
      shiftY(-0.02F);
      SAND_BOXES.add(makeBox(6.0, 14.0, 6.0, 4.0, 1.0, 4.0));
      SAND_BOXES.add(makeBox(5.0, 15.0, 5.0, 6.0, 1.0, 6.0));
      SAND_BOXES.add(makeBox(4.0, 16.0, 4.0, 8.0, 2.0, 8.0));
      SAND_BOXES.add(makeBox(3.0, 18.0, 3.0, 10.0, 3.0, 10.0));
      SAND_BOXES.add(makeBox(2.0, 21.0, 2.0, 12.0, 7.0, 12.0));
      shiftY(0.01F);
   }
}
