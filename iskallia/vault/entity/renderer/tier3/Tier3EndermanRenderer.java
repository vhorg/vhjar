package iskallia.vault.entity.renderer.tier3;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3EndermanEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3EndermanModel;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Tier3EndermanRenderer extends MobRenderer<Tier3EndermanEntity, Tier3EndermanModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/enderman.png");
   private final Random random = new Random();

   public Tier3EndermanRenderer(Context context) {
      super(context, new Tier3EndermanModel(context.bakeLayer(ModModelLayers.T3_ENDERMAN)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3EndermanEntity entity) {
      return TEXTURE;
   }

   public void render(
      Tier3EndermanEntity pEntity, float pEntityYaw, float pPartialTicks, @Nonnull PoseStack pMatrixStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight
   ) {
      BlockState blockstate = pEntity.getCarriedBlock();
      Tier3EndermanModel model = (Tier3EndermanModel)this.getModel();
      model.carrying = blockstate != null;
      model.creepy = pEntity.isCreepy();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   @Nonnull
   public Vec3 getRenderOffset(Tier3EndermanEntity pEntity, float pPartialTicks) {
      if (pEntity.isCreepy()) {
         double d0 = 0.02;
         return new Vec3(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
      } else {
         return super.getRenderOffset(pEntity, pPartialTicks);
      }
   }
}