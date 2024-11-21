package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultStormArrow;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StormArrowRenderer extends ArrowRenderer<VaultStormArrow> {
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/storm_arrow.png");
   public static final ResourceLocation TEXTURE_LOCATION_BLIZZARD = VaultMod.id("textures/entity/blizzard_shard.png");

   public StormArrowRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultStormArrow entity) {
      return entity.getStormArrowType() == VaultStormArrow.StormType.BASE ? TEXTURE_LOCATION : TEXTURE_LOCATION_BLIZZARD;
   }

   @ParametersAreNonnullByDefault
   public void render(VaultStormArrow entity, float pEntityYaw, float partialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      super.render(entity, pEntityYaw, partialTicks, pMatrixStack, pBuffer, pPackedLight);
   }
}
