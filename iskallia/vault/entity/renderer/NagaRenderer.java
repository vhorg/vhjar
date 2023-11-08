package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.NagaEntity;
import iskallia.vault.entity.model.NagaModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NagaRenderer extends GeoEntityRenderer<NagaEntity> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/champion.png");

   public NagaRenderer(Context renderManager) {
      super(renderManager, new NagaModel());
      this.shadowRadius = 1.0F;
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull NagaEntity entity) {
      return TEXTURE;
   }

   public RenderType getRenderType(
      NagaEntity entity,
      float partialTick,
      PoseStack poseStack,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      int packedLight,
      ResourceLocation texture
   ) {
      return super.getRenderType(entity, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
   }
}
