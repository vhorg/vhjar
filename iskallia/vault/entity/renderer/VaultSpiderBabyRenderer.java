package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultSpiderBabyEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.VaultSpiderBabyModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class VaultSpiderBabyRenderer extends MobRenderer<VaultSpiderBabyEntity, VaultSpiderBabyModel<VaultSpiderBabyEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/spider_baby.png");

   public VaultSpiderBabyRenderer(Context context) {
      super(context, new VaultSpiderBabyModel(context.bakeLayer(ModModelLayers.VAULT_SPIDER_BABY)), 0.25F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultSpiderBabyEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected float getFlipDegrees(@Nonnull VaultSpiderBabyEntity pLivingEntity) {
      return 180.0F;
   }

   protected void scale(@Nonnull VaultSpiderBabyEntity entity, PoseStack pMatrixStack, float pPartialTickTime) {
      float scale = 0.3F;
      pMatrixStack.scale(scale, scale, scale);
   }

   public void render(
      @Nonnull VaultSpiderBabyEntity entity,
      float pEntityYaw,
      float pPartialTicks,
      @Nonnull PoseStack pMatrixStack,
      @Nonnull MultiBufferSource buffers,
      int pPackedLight
   ) {
      super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, buffers, pPackedLight);
   }
}
