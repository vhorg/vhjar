package iskallia.vault.entity.renderer.overgrown_woodman;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.overgrown_woodman.Tier1OvergrownWoodmanEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.overgrown_woodman.Tier1OvergrownWoodmanModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier1OvergrownWoodmanRenderer extends HumanoidMobRenderer<Tier1OvergrownWoodmanEntity, Tier1OvergrownWoodmanModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/overgrown_woodman/t1.png");

   public Tier1OvergrownWoodmanRenderer(Context context) {
      super(context, new Tier1OvergrownWoodmanModel(context.bakeLayer(ModModelLayers.T1_OVERGROWN_WOODMAN)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1OvergrownWoodmanEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier1OvergrownWoodmanEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
