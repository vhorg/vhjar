package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.SoulPlaqueBlock;
import iskallia.vault.block.entity.SoulPlaqueTileEntity;
import iskallia.vault.client.util.LightmapUtil;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModShaders;
import iskallia.vault.task.renderer.context.RendererContext;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class SoulPlaqueRenderer implements BlockEntityRenderer<SoulPlaqueTileEntity> {
   private final Font font;
   private final ItemRenderer itemRenderer;
   private final StatuePlayerModel model;

   public SoulPlaqueRenderer(Context context) {
      this.font = context.getFont();
      this.itemRenderer = Minecraft.getInstance().getItemRenderer();
      this.model = new StatuePlayerModel(context);
   }

   public void render(SoulPlaqueTileEntity entity, float pPartialTick, PoseStack matrices, MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
      ResourceLocation skinLocation = entity.getSkin().getLocationSkin();
      RenderType renderType = this.model.renderType(skinLocation);
      VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
      BlockState blockState = entity.getBlockState();
      Direction direction = (Direction)blockState.getValue(SoulPlaqueBlock.FACING);
      matrices.pushPose();
      Color color = new Color(-6646101);
      ModShaders.getColorizePositionTexShader()
         .withColorize(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F)
         .withBrightness(LightmapUtil.getLightmapBrightness(pPackedLight))
         .withGrayscale(0.45F)
         .enable();
      matrices.translate(0.5, 0.5, 0.5);
      matrices.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));
      float headScale = 1.05F;
      matrices.scale(headScale, headScale, 0.35F);
      matrices.translate(0.0, -0.15F, 1.1F);
      matrices.scale(-1.0F, -1.0F, 1.0F);
      this.model.hat.render(matrices, vertexBuilder, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      this.model.head.render(matrices, vertexBuilder, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      if (buffer instanceof BufferSource) {
         ((BufferSource)buffer).endBatch(renderType);
      }

      matrices.pushPose();
      matrices.scale(0.02F, 0.02F, 1.0F);
      matrices.translate(0.0, 5.0, -0.05F);
      RendererContext context = new RendererContext(matrices, pPartialTick, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), this.font);
      context.renderText(new TextComponent(entity.getScore() + ""), 0.0F, 5.0F, true, true);
      matrices.popPose();
      matrices.popPose();
   }
}
