package iskallia.vault.entity.renderer.winterwalker;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.winterwalker.Tier5WinterwalkerEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.winterwalker.Tier5WinterwalkerModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier5WinterwalkerRenderer extends HumanoidMobRenderer<Tier5WinterwalkerEntity, Tier5WinterwalkerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/winterwalker/t5.png");

   public Tier5WinterwalkerRenderer(Context context) {
      super(context, new Tier5WinterwalkerModel(context.bakeLayer(ModModelLayers.T5_WINTERWALKER)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier5WinterwalkerEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier5WinterwalkerEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
