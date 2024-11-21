package iskallia.vault.entity.renderer.overgrown_woodman;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_woodman.Tier2OvergrownWoodmanEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_woodman.Tier2OvergrownWoodmanModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2OvergrownWoodmanRenderer extends HumanoidMobRenderer<Tier2OvergrownWoodmanEntity, Tier2OvergrownWoodmanModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_woodman/t2.png");

   public Tier2OvergrownWoodmanRenderer(Context context) {
      super(context, new Tier2OvergrownWoodmanModel(context.bakeLayer(ModModelLayers.T2_OVERGROWN_WOODMAN)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2OvergrownWoodmanEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier2OvergrownWoodmanEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
