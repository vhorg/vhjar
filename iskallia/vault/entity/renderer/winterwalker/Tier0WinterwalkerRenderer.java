package iskallia.vault.entity.renderer.winterwalker;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.winterwalker.Tier0WinterwalkerEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.winterwalker.Tier0WinterwalkerModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier0WinterwalkerRenderer extends HumanoidMobRenderer<Tier0WinterwalkerEntity, Tier0WinterwalkerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/winterwalker/t0.png");

   public Tier0WinterwalkerRenderer(Context context) {
      super(context, new Tier0WinterwalkerModel(context.bakeLayer(ModModelLayers.T0_WINTERWALKER)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier0WinterwalkerEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier0WinterwalkerEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
