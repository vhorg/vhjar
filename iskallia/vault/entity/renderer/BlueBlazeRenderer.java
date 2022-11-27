package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class BlueBlazeRenderer extends BlazeRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/blue_blaze.png");

   public BlueBlazeRenderer(Context context) {
      super(context);
   }

   protected void scale(Blaze entitylivingbase, PoseStack matrixStack, float partialTickTime) {
      super.scale(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.scale(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation getTextureLocation(Blaze entity) {
      return TEXTURE;
   }
}
