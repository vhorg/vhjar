package iskallia.vault.entity.renderer.guardian;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.guardian.ButcherGuardianEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.guardian.ButcherGuardianModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class ButcherGuardianRenderer {
   public static class Arbalist extends HumanoidMobRenderer<ButcherGuardianEntity, ButcherGuardianModel.Arbalist> {
      public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/guardian/butcher_arbalist.png");

      public Arbalist(Context context) {
         super(context, new ButcherGuardianModel.Arbalist(context.bakeLayer(ModModelLayers.BUTCHER_GUARDIAN_ARBALIST)), 0.6F);
      }

      protected void scale(@Nonnull ButcherGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
         super.scale(entity, matrixStack, partialTickTime);
         matrixStack.scale(1.1F, 1.1F, 1.1F);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull ButcherGuardianEntity entity) {
         return TEXTURE;
      }
   }

   public static class Bruiser extends HumanoidMobRenderer<ButcherGuardianEntity, ButcherGuardianModel.Bruiser> {
      public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/guardian/butcher_bruiser.png");

      public Bruiser(Context context) {
         super(context, new ButcherGuardianModel.Bruiser(context.bakeLayer(ModModelLayers.BUTCHER_GUARDIAN_BRUISER)), 0.6F);
      }

      protected void scale(@Nonnull ButcherGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
         super.scale(entity, matrixStack, partialTickTime);
         matrixStack.scale(1.1F, 1.1F, 1.1F);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull ButcherGuardianEntity entity) {
         return TEXTURE;
      }
   }
}
