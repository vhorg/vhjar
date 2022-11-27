package iskallia.vault.entity.renderer.tier1;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tier1StrayClothingLayer<T extends Mob & RangedAttackMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/stray_overlay.png");
   private final SkeletonModel<T> layerModel;

   public Tier1StrayClothingLayer(RenderLayerParent<T, M> p_174544_, EntityModelSet p_174545_) {
      super(p_174544_);
      this.layerModel = new SkeletonModel(p_174545_.bakeLayer(ModelLayers.STRAY_OUTER_LAYER));
   }

   public void render(
      @Nonnull PoseStack pMatrixStack,
      @Nonnull MultiBufferSource pBuffer,
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
         this.layerModel,
         TEXTURE,
         pMatrixStack,
         pBuffer,
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
