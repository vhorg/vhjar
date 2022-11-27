package iskallia.vault.block.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.client.util.RenderTypeDecorator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public class HourglassRenderer implements BlockEntityRenderer<HourglassTileEntity> {
   private static final ResourceLocation SAND_TEXTURE = VaultMod.id("textures/block/hourglass_sand.png");
   private static List<AABB> SAND_BOXES = new ArrayList<>();
   private static final int totalHeight = 28;

   public HourglassRenderer(Context context) {
   }

   public void render(HourglassTileEntity te, float partialTicks, PoseStack renderStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
      ModelPart sandBoxes = this.prepareSandRender(te.getFilledPercentage());
      RenderType wrapped = RenderTypeDecorator.decorate(
         RenderType.solid(),
         () -> GlStateManager._bindTexture(Minecraft.getInstance().getTextureManager().getTexture(SAND_TEXTURE).getId()),
         () -> RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS)
      );
      RenderType other = RenderType.entitySolid(SAND_TEXTURE);
      renderStack.pushPose();
      renderStack.translate(0.01, 0.125, 0.01);
      renderStack.scale(0.98F, 1.0F, 0.98F);
      VertexConsumer vb = buffers.getBuffer(wrapped);
      renderStack.popPose();
      buffers.getBuffer(RenderType.lines());
   }

   private ModelPart prepareSandRender(float percentage) {
      float heightPart = totalHeight * Mth.clamp(percentage, 0.0F, 1.0F);

      for (AABB box : SAND_BOXES) {
         float ySize = (float)box.getYsize();
         float remainingHeight = heightPart - ySize;
         if (!(remainingHeight >= 0.0F)) {
            float part = heightPart / ySize;
            break;
         }

         heightPart = (float)(heightPart - box.getYsize());
      }

      return null;
   }

   private static void shiftY(float y) {
      SAND_BOXES = SAND_BOXES.stream().map(box -> box.move(0.0, y, 0.0)).collect(Collectors.toList());
   }

   private static AABB makeBox(double x, double y, double z, double width, double height, double depth) {
      return new AABB(x, y, z, x + width, y + height, z + depth);
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
