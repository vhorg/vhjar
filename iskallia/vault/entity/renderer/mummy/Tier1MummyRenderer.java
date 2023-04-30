package iskallia.vault.entity.renderer.mummy;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.mummy.Tier1MummyEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mummy.Tier1MummyModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier1MummyRenderer extends HumanoidMobRenderer<Tier1MummyEntity, Tier1MummyModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/mummy/t1.png");

   public Tier1MummyRenderer(Context context) {
      super(context, new Tier1MummyModel(context.bakeLayer(ModModelLayers.T1_MUMMY)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1MummyEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier1MummyEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
