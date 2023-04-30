package iskallia.vault.entity.renderer.mummy;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.mummy.Tier0MummyEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mummy.Tier0MummyModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier0MummyRenderer extends HumanoidMobRenderer<Tier0MummyEntity, Tier0MummyModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/mummy/t0.png");

   public Tier0MummyRenderer(Context context) {
      super(context, new Tier0MummyModel(context.bakeLayer(ModModelLayers.T0_MUMMY)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier0MummyEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier0MummyEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
