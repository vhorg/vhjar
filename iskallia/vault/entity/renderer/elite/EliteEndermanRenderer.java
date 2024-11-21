package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EliteEndermanEntity;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EliteEndermanRenderer extends MobRenderer<EliteEndermanEntity, EndermanModel<EliteEndermanEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/enderman.png");
   private final Random random = new Random();

   public EliteEndermanRenderer(Context context) {
      super(context, new EndermanModel(context.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
      this.addLayer(new EliteEnderEyesLayer(this));
      int ornamentCount = EliteEndermanEntity.getVisibleOrnamentCount(1.0);

      for (int i = 0; i < ornamentCount; i++) {
         this.addLayer(new EliteEnderOrnamentLayer(this, context.getModelSet(), i));
      }
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull EliteEndermanEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull EliteEndermanEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.2F;
      poseStack.scale(scale, scale, scale);
   }

   @Nonnull
   public Vec3 getRenderOffset(EliteEndermanEntity entity, float partialTicks) {
      if (entity.isCreepy()) {
         double d0 = 0.02;
         return new Vec3(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
      } else {
         return super.getRenderOffset(entity, partialTicks);
      }
   }

   public void render(
      @Nonnull EliteEndermanEntity entity,
      float pEntityYaw,
      float pPartialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int pPackedLight
   ) {
      BlockState blockstate = entity.getCarriedBlock();
      EndermanModel<EliteEndermanEntity> endermanmodel = (EndermanModel<EliteEndermanEntity>)this.getModel();
      endermanmodel.carrying = blockstate != null;
      endermanmodel.creepy = entity.isCreepy();
      super.render(entity, pEntityYaw, pPartialTicks, matrixStack, buffer, pPackedLight);
   }
}
