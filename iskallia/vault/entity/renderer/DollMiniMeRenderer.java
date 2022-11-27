package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.entity.DollMiniMeEntity;
import java.util.List;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class DollMiniMeRenderer extends EntityRenderer<DollMiniMeEntity> {
   private final DollMiniMeRenderer.MiniMePlayerRenderer steveMiniMeRenderer;
   private final DollMiniMeRenderer.MiniMePlayerRenderer alexMiniMeRenderer;

   public DollMiniMeRenderer(Context context) {
      super(context);
      PlayerModel<DollMiniMeEntity> steveMiniModel = new PlayerModel<DollMiniMeEntity>(context.getModelSet().bakeLayer(ModelLayers.PLAYER), false) {
         protected Iterable<ModelPart> headParts() {
            return List.of(this.head, this.hat);
         }
      };
      steveMiniModel.young = true;
      this.steveMiniMeRenderer = new DollMiniMeRenderer.MiniMePlayerRenderer(context, steveMiniModel);
      PlayerModel<DollMiniMeEntity> alexMiniModel = new PlayerModel<DollMiniMeEntity>(context.getModelSet().bakeLayer(ModelLayers.PLAYER_SLIM), true) {
         protected Iterable<ModelPart> headParts() {
            return List.of(this.head, this.hat);
         }
      };
      alexMiniModel.young = true;
      this.alexMiniMeRenderer = new DollMiniMeRenderer.MiniMePlayerRenderer(context, alexMiniModel);
   }

   public void render(DollMiniMeEntity miniMe, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      if (miniMe.hasSlimSkin()) {
         this.alexMiniMeRenderer.render(miniMe, entityYaw, partialTicks, poseStack, buffer, packedLight);
      } else {
         this.steveMiniMeRenderer.render(miniMe, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   public ResourceLocation getTextureLocation(DollMiniMeEntity entity) {
      return DefaultPlayerSkin.getDefaultSkin();
   }

   private static class MiniMePlayerRenderer extends LivingEntityRenderer<DollMiniMeEntity, PlayerModel<DollMiniMeEntity>> {
      private PlayerSkinUpdater playerSkinUpdater = new PlayerSkinUpdater();

      public MiniMePlayerRenderer(Context context, PlayerModel<DollMiniMeEntity> model) {
         super(context, model, 0.5F);
      }

      public ResourceLocation getTextureLocation(DollMiniMeEntity miniMe) {
         return miniMe.getSkinLocation()
            .orElseGet(() -> miniMe.getGameProfile().map(gp -> this.playerSkinUpdater.updatePlayerSkin(miniMe, gp)).orElse(DefaultPlayerSkin.getDefaultSkin()));
      }

      protected void scale(DollMiniMeEntity miniMe, PoseStack poseStack, float partialTickTime) {
         poseStack.scale(0.9375F, 0.9375F, 0.9375F);
      }

      protected boolean shouldShowName(DollMiniMeEntity entity) {
         return false;
      }
   }
}
