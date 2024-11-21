package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.boss.GolemBossEntity;
import iskallia.vault.entity.model.GolemBossModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GolemBossRenderer extends GeoEntityRenderer<GolemBossEntity> {
   private static final GolemBossModel model = new GolemBossModel();

   public GolemBossRenderer(Context context) {
      super(context, model);
   }

   public void render(GolemBossEntity entity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      model.getModel(model.getModelLocation(entity)).getBone("RightElbow").ifPresent(b -> b.setHidden(!entity.showsRightHand()));
      model.getModel(model.getModelLocation(entity)).getBone("LeftElbow").ifPresent(b -> b.setHidden(!entity.showsLeftHand()));
      super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      VaultBossTraitRenderer.renderTraits(entity);
   }
}
