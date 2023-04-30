package iskallia.vault.entity.renderer.overgrown_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_zombie.Tier3OvergrownZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_zombie.Tier3OvergrownZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3OvergrownZombieRenderer extends HumanoidMobRenderer<Tier3OvergrownZombieEntity, Tier3OvergrownZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_zombie/t3.png");

   public Tier3OvergrownZombieRenderer(Context context) {
      super(context, new Tier3OvergrownZombieModel(context.bakeLayer(ModModelLayers.T3_OVERGROWN_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3OvergrownZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier3OvergrownZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
