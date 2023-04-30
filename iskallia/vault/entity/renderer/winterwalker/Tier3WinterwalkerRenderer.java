package iskallia.vault.entity.renderer.winterwalker;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.winterwalker.Tier3WinterwalkerEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.winterwalker.Tier3WinterwalkerModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3WinterwalkerRenderer extends HumanoidMobRenderer<Tier3WinterwalkerEntity, Tier3WinterwalkerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/winterwalker/t3.png");

   public Tier3WinterwalkerRenderer(Context context) {
      super(context, new Tier3WinterwalkerModel(context.bakeLayer(ModModelLayers.T3_WINTERWALKER)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3WinterwalkerEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier3WinterwalkerEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
