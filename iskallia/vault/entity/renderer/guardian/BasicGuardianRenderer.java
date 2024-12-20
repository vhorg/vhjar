package iskallia.vault.entity.renderer.guardian;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.guardian.BasicGuardianEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class BasicGuardianRenderer extends HumanoidMobRenderer<BasicGuardianEntity, PiglinModel<BasicGuardianEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/guardian/basic.png");

   public BasicGuardianRenderer(Context context) {
      super(context, new PiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), 0.6F);
   }

   protected void scale(@Nonnull BasicGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
      super.scale(entity, matrixStack, partialTickTime);
      matrixStack.scale(1.1F, 1.1F, 1.1F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull BasicGuardianEntity entity) {
      return TEXTURE;
   }
}
