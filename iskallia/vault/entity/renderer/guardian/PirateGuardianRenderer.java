package iskallia.vault.entity.renderer.guardian;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.guardian.PirateGuardianEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.guardian.PirateGuardianModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class PirateGuardianRenderer {
   public static class Arbalist extends HumanoidMobRenderer<PirateGuardianEntity, PirateGuardianModel.Arbalist> {
      public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/guardian/pirate_arbalist.png");

      public Arbalist(Context context) {
         super(context, new PirateGuardianModel.Arbalist(context.bakeLayer(ModModelLayers.PIRATE_GUARDIAN_ARBALIST)), 0.6F);
      }

      protected void scale(@Nonnull PirateGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
         super.scale(entity, matrixStack, partialTickTime);
         double time = entity.tickCount / 40.0 + System.currentTimeMillis() / 500.0;
         matrixStack.translate(0.0, Math.sin(time) * 0.1 + 0.2, 0.0);
         matrixStack.scale(1.1F, 1.1F, 1.1F);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull PirateGuardianEntity entity) {
         return TEXTURE;
      }
   }

   public static class Bruiser extends HumanoidMobRenderer<PirateGuardianEntity, PirateGuardianModel.Bruiser> {
      public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/guardian/pirate_bruiser.png");

      public Bruiser(Context context) {
         super(context, new PirateGuardianModel.Bruiser(context.bakeLayer(ModModelLayers.PIRATE_GUARDIAN_BRUISER)), 0.6F);
      }

      protected void scale(@Nonnull PirateGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
         super.scale(entity, matrixStack, partialTickTime);
         double time = entity.tickCount / 40.0 + System.currentTimeMillis() / 500.0;
         matrixStack.translate(0.0, Math.sin(time) * 0.1 + 0.2, 0.0);
         matrixStack.scale(1.1F, 1.1F, 1.1F);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull PirateGuardianEntity entity) {
         return TEXTURE;
      }
   }
}
