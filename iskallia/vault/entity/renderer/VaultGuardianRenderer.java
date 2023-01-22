package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultGuardianEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class VaultGuardianRenderer extends HumanoidMobRenderer<VaultGuardianEntity, PiglinModel<VaultGuardianEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/vault_guardian.png");

   public VaultGuardianRenderer(Context context) {
      super(context, new PiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), 1.0F);
   }

   protected void scale(@Nonnull VaultGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
      super.scale(entity, matrixStack, partialTickTime);
      matrixStack.scale(1.1F, 1.2F, 1.1F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultGuardianEntity entity) {
      return TEXTURE;
   }
}
