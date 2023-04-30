package iskallia.vault.entity.renderer.winterwalker;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.winterwalker.Tier2WinterwalkerEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.winterwalker.Tier2WinterwalkerModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2WinterwalkerRenderer extends HumanoidMobRenderer<Tier2WinterwalkerEntity, Tier2WinterwalkerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/winterwalker/t2.png");

   public Tier2WinterwalkerRenderer(Context context) {
      super(context, new Tier2WinterwalkerModel(context.bakeLayer(ModModelLayers.T2_WINTERWALKER)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2WinterwalkerEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier2WinterwalkerEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
