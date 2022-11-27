package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class RobotRenderer extends IronGolemRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/robot.png");

   public RobotRenderer(Context context) {
      super(context);
   }

   protected void scale(IronGolem entitylivingbase, PoseStack matrixStack, float partialTickTime) {
      super.scale(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.scale(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation getTextureLocation(IronGolem entity) {
      return TEXTURE;
   }
}
