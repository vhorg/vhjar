package iskallia.vault.entity.renderer.tier2;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier2.Tier2CreeperModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class Tier2CreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier2/creeper.png");

   public Tier2CreeperRenderer(Context context) {
      super(context, new Tier2CreeperModel(context.bakeLayer(ModModelLayers.T2_CREEPER)), 0.5F);
      this.addLayer(new CreeperPowerLayer(this, context.getModelSet()));
   }

   protected void scale(Creeper pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
      float f = pLivingEntity.getSwelling(pPartialTickTime);
      float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
      f = Mth.clamp(f, 0.0F, 1.0F);
      f *= f;
      f *= f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      pMatrixStack.scale(f2, f3, f2);
   }

   protected float getWhiteOverlayProgress(Creeper pLivingEntity, float pPartialTicks) {
      float f = pLivingEntity.getSwelling(pPartialTicks);
      return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Creeper entity) {
      return TEXTURE;
   }
}
