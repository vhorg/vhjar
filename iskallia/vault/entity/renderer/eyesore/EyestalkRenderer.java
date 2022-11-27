package iskallia.vault.entity.renderer.eyesore;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.eyesore.EyestalkEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.eyesore.EyestalkModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EyestalkRenderer extends MobRenderer<EyestalkEntity, EyestalkModel> {
   public static final ResourceLocation DEFAULT_TEXTURE = VaultMod.id("textures/entity/eyesore/eyestalk.png");

   public EyestalkRenderer(Context context) {
      super(context, new EyestalkModel(context.bakeLayer(ModModelLayers.EYESTALK)), 0.2F);
   }

   protected void scale(EyestalkEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      super.scale(entitylivingbaseIn, matrixStackIn, partialTickTime);
   }

   protected void renderNameTag(EyestalkEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
   }

   protected boolean shouldShowName(EyestalkEntity entity) {
      return false;
   }

   public ResourceLocation getTextureLocation(EyestalkEntity entity) {
      return DEFAULT_TEXTURE;
   }
}
