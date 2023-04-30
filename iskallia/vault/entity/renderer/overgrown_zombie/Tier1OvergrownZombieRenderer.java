package iskallia.vault.entity.renderer.overgrown_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_zombie.Tier1OvergrownZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_zombie.Tier1OvergrownZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier1OvergrownZombieRenderer extends HumanoidMobRenderer<Tier1OvergrownZombieEntity, Tier1OvergrownZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_zombie/t1.png");

   public Tier1OvergrownZombieRenderer(Context context) {
      super(context, new Tier1OvergrownZombieModel(context.bakeLayer(ModModelLayers.T1_OVERGROWN_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1OvergrownZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier1OvergrownZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
