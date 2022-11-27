package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EliteStrayEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EliteStrayModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class EliteStrayRenderer extends HumanoidMobRenderer<EliteStrayEntity, EliteStrayModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/stray.png");

   public EliteStrayRenderer(Context context) {
      super(context, new EliteStrayModel(context.bakeLayer(ModModelLayers.ELITE_STRAY)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull EliteStrayEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull EliteStrayEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.4F;
      poseStack.scale(scale, scale, scale);
   }
}
