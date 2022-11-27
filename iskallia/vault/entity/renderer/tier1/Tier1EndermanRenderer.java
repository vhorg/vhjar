package iskallia.vault.entity.renderer.tier1;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier1.Tier1EndermanEntity;
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

public class Tier1EndermanRenderer extends MobRenderer<Tier1EndermanEntity, EndermanModel<Tier1EndermanEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/enderman.png");
   private final Random random = new Random();

   public Tier1EndermanRenderer(Context context) {
      super(context, new EndermanModel(context.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
      this.addLayer(new Tier1EndermanEyesLayer(this));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1EndermanEntity entity) {
      return TEXTURE;
   }

   public void render(
      Tier1EndermanEntity pEntity, float pEntityYaw, float pPartialTicks, @Nonnull PoseStack pMatrixStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight
   ) {
      BlockState blockstate = pEntity.getCarriedBlock();
      EndermanModel<Tier1EndermanEntity> endermanmodel = (EndermanModel<Tier1EndermanEntity>)this.getModel();
      endermanmodel.carrying = blockstate != null;
      endermanmodel.creepy = pEntity.isCreepy();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   @Nonnull
   public Vec3 getRenderOffset(Tier1EndermanEntity pEntity, float pPartialTicks) {
      if (pEntity.isCreepy()) {
         double d0 = 0.02;
         return new Vec3(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
      } else {
         return super.getRenderOffset(pEntity, pPartialTicks);
      }
   }
}
