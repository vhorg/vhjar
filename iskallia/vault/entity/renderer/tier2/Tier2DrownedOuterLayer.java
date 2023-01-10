package iskallia.vault.entity.renderer.tier2;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Drowned;

public class Tier2DrownedOuterLayer<T extends Drowned> extends RenderLayer<T, DrownedModel<T>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier2/drowned_outer_layer.png");
   private final DrownedModel<T> model;

   public Tier2DrownedOuterLayer(RenderLayerParent<T, DrownedModel<T>> pRenderer, EntityModelSet entityModelSet) {
      super(pRenderer);
      this.model = new DrownedModel(entityModelSet.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
   }

   public void render(
      @Nonnull PoseStack pMatrixStack,
      @Nonnull MultiBufferSource buffer,
      int pPackedLight,
      @Nonnull T entity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      coloredCutoutModelCopyLayerRender(
         this.getParentModel(),
         this.model,
         TEXTURE,
         pMatrixStack,
         buffer,
         pPackedLight,
         entity,
         pLimbSwing,
         pLimbSwingAmount,
         pAgeInTicks,
         pNetHeadYaw,
         pHeadPitch,
         pPartialTicks,
         1.0F,
         1.0F,
         1.0F
      );
   }
}
