package iskallia.vault.entity.renderer.overgrown_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_zombie.Tier0OvergrownZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_zombie.Tier0OvergrownZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier0OvergrownZombieRenderer extends HumanoidMobRenderer<Tier0OvergrownZombieEntity, Tier0OvergrownZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_zombie/t0.png");

   public Tier0OvergrownZombieRenderer(Context context) {
      super(context, new Tier0OvergrownZombieModel(context.bakeLayer(ModModelLayers.T0_OVERGROWN_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier0OvergrownZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier0OvergrownZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
