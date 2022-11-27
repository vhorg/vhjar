package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;

public class EliteWitchRenderer extends WitchRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/witch.png");

   public EliteWitchRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(Witch entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(Witch entity, PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      poseStack.scale(1.2F, 1.2F, 1.2F);
   }
}
