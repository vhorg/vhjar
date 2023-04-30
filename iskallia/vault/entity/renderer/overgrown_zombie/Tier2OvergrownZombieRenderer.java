package iskallia.vault.entity.renderer.overgrown_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_zombie.Tier2OvergrownZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_zombie.Tier2OvergrownZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2OvergrownZombieRenderer extends HumanoidMobRenderer<Tier2OvergrownZombieEntity, Tier2OvergrownZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_zombie/t2.png");

   public Tier2OvergrownZombieRenderer(Context context) {
      super(context, new Tier2OvergrownZombieModel(context.bakeLayer(ModModelLayers.T2_OVERGROWN_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2OvergrownZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier2OvergrownZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
