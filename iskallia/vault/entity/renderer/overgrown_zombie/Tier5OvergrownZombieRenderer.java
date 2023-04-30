package iskallia.vault.entity.renderer.overgrown_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_zombie.Tier5OvergrownZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_zombie.Tier5OvergrownZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier5OvergrownZombieRenderer extends HumanoidMobRenderer<Tier5OvergrownZombieEntity, Tier5OvergrownZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_zombie/t5.png");

   public Tier5OvergrownZombieRenderer(Context context) {
      super(context, new Tier5OvergrownZombieModel(context.bakeLayer(ModModelLayers.T5_OVERGROWN_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier5OvergrownZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier5OvergrownZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
