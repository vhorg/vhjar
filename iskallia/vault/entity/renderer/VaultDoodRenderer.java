package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultDoodEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.VaultDoodModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class VaultDoodRenderer extends MobRenderer<VaultDoodEntity, VaultDoodModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/vault_dood.png");

   public VaultDoodRenderer(Context context) {
      super(context, new VaultDoodModel(context.bakeLayer(ModModelLayers.VAULT_DOOD)), 0.5F);
      this.addLayer(new VaultDoodCrackinessLayer(this));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultDoodEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull VaultDoodEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 0.7F;
      poseStack.scale(scale, scale, scale);
   }
}
