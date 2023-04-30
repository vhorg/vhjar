package iskallia.vault.entity.renderer.mummy;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.mummy.Tier2MummyEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mummy.Tier2MummyModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2MummyRenderer extends HumanoidMobRenderer<Tier2MummyEntity, Tier2MummyModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/mummy/t2.png");

   public Tier2MummyRenderer(Context context) {
      super(context, new Tier2MummyModel(context.bakeLayer(ModModelLayers.T2_MUMMY)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2MummyEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier2MummyEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
