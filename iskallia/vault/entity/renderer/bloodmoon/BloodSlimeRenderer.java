package iskallia.vault.entity.renderer.bloodmoon;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.BloodSlimeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.BloodSlimeModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BloodSlimeRenderer extends MobRenderer<BloodSlimeEntity, SlimeModel<BloodSlimeEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/slime.png");

   public BloodSlimeRenderer(Context ctx) {
      super(ctx, new BloodSlimeModel(ctx.bakeLayer(ModModelLayers.BLOOD_SLIME)), 0.25F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull BloodSlimeEntity entity) {
      return TEXTURE_LOCATION;
   }

   public void render(BloodSlimeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      this.shadowRadius = 0.25F * pEntity.getSize();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   protected void scale(BloodSlimeEntity pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
      float f = 0.999F;
      pMatrixStack.scale(0.5F, 0.5F, 0.5F);
      pMatrixStack.translate(0.0, 0.001F, 0.0);
      float f1 = pLivingEntity.getSize();
      float f2 = Mth.lerp(pPartialTickTime, pLivingEntity.oSquish, pLivingEntity.squish) / (f1 * 0.5F + 1.0F);
      float f3 = 1.0F / (f2 + 1.0F);
      pMatrixStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }
}
