package iskallia.vault.entity.renderer.winterwalker;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.winterwalker.Tier1WinterwalkerEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.winterwalker.Tier1WinterwalkerModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier1WinterwalkerRenderer extends HumanoidMobRenderer<Tier1WinterwalkerEntity, Tier1WinterwalkerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/winterwalker/t1.png");

   public Tier1WinterwalkerRenderer(Context context) {
      super(context, new Tier1WinterwalkerModel(context.bakeLayer(ModModelLayers.T1_WINTERWALKER)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1WinterwalkerEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier1WinterwalkerEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
