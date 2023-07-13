package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.block.entity.MobBarrierTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.Level;

public class MobBarrierRenderer implements BlockEntityRenderer<MobBarrierTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();

   public MobBarrierRenderer(Context context) {
   }

   public void render(
      MobBarrierTileEntity mobBarrierTile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = mobBarrierTile.getLevel();
      if (world != null) {
         if (this.mc.player != null && (this.mc.player.isCreative() || this.mc.player.isSpectator())) {
            int colorDuration = 2000;
            double position = (this.mc.player.tickCount * 20 + (int)(partialTicks * 20.0F)) % 2000 / 2000.0;
            int red = (int)(Math.cos(position * Math.PI * 2.0) * 127.0 + 128.0);
            int green = (int)(Math.cos((position + 0.3333333333333333) * Math.PI * 2.0) * 127.0 + 128.0);
            int blue = (int)(Math.cos((position + 0.6666666666666666) * Math.PI * 2.0) * 127.0 + 128.0);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(matrixStack, vertexconsumer, 0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F, red, green, blue, 1.0F, red, green, blue);
         }
      }
   }
}
