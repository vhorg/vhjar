package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.entity.VaultStormEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StormRenderer extends EntityRenderer<VaultStormEntity> {
   public StormRenderer(Context p_174420_) {
      super(p_174420_);
   }

   public void render(VaultStormEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
   }

   public ResourceLocation getTextureLocation(VaultStormEntity pEntity) {
      return null;
   }
}
