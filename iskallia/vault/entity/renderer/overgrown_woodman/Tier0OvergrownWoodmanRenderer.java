package iskallia.vault.entity.renderer.overgrown_woodman;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_woodman.Tier0OvergrownWoodmanEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_woodman.Tier0OvergrownWoodmanModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier0OvergrownWoodmanRenderer extends HumanoidMobRenderer<Tier0OvergrownWoodmanEntity, Tier0OvergrownWoodmanModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_woodman/t0.png");

   public Tier0OvergrownWoodmanRenderer(Context context) {
      super(context, new Tier0OvergrownWoodmanModel(context.bakeLayer(ModModelLayers.T0_OVERGROWN_WOODMAN)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier0OvergrownWoodmanEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier0OvergrownWoodmanEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
