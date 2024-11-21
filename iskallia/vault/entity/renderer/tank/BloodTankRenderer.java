package iskallia.vault.entity.renderer.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tank.BloodTankEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tank.BloodTankModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BloodTankRenderer extends MobRenderer<BloodTankEntity, BloodTankModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/tank/bloodhorde.png");

   public BloodTankRenderer(Context context) {
      super(context, new BloodTankModel(context.bakeLayer(ModModelLayers.BLOOD_TANK)), 0.7F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull BloodTankEntity pEntity) {
      return TEXTURE_LOCATION;
   }

   protected void setupRotations(
      @NotNull BloodTankEntity pEntityLiving, @NotNull PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks
   ) {
      super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
      if (!(pEntityLiving.animationSpeed < 0.01)) {
         float f = 13.0F;
         float f1 = pEntityLiving.animationPosition - pEntityLiving.animationSpeed * (1.0F - pPartialTicks) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
      }
   }
}
