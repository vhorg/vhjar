package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.model.GolemBossModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GolemBossRenderer extends GeoEntityRenderer<VaultBossEntity> {
   public GolemBossRenderer(Context context) {
      super(context, new GolemBossModel());
   }

   public void render(VaultBossEntity entity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      VaultBossTraitRenderer.renderTraits(entity);
   }
}
