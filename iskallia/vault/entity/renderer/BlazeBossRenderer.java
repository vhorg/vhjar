package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.boss.VaultBossEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class BlazeBossRenderer extends MobRenderer<VaultBossEntity, BlazeModel<VaultBossEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/blaze_boss.png");

   public BlazeBossRenderer(Context context) {
      super(context, new BlazeModel(context.bakeLayer(ModelLayers.BLAZE)), 0.5F);
   }

   public void render(VaultBossEntity entity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      VaultBossTraitRenderer.renderTraits(entity);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultBossEntity entity) {
      return TEXTURE_LOCATION;
   }
}
