package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EliteSpiderModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class BlackWidowBossRenderer extends MobRenderer<VaultBossEntity, EliteSpiderModel<VaultBossEntity>> {
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/spider.png");
   public static final RenderType ELITE_SPIDER_EYES = RenderType.eyes(VaultMod.id("textures/entity/elite/spider_eyes.png"));

   public BlackWidowBossRenderer(Context context) {
      super(context, new EliteSpiderModel(context.bakeLayer(ModModelLayers.ELITE_SPIDER)), 1.15F);
   }

   protected float getFlipDegrees(@Nonnull VaultBossEntity entity) {
      return 180.0F;
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultBossEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull VaultBossEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      poseStack.scale(1.2F, 1.2F, 1.2F);
   }
}
