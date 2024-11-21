package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.model.BoogiemanBossModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BoogiemanBossRenderer extends GeoEntityRenderer<VaultBossEntity> {
   public BoogiemanBossRenderer(Context context) {
      super(context, new BoogiemanBossModel());
   }

   public void render(VaultBossEntity entity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
      poseStack.pushPose();
      poseStack.scale(2.0F, 2.0F, 2.0F);
      super.render(entity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
      VaultBossTraitRenderer.renderTraits(entity);
      poseStack.popPose();
   }
}
