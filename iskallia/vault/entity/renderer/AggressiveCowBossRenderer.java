package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.animal.Cow;

public class AggressiveCowBossRenderer extends CowRenderer {
   public AggressiveCowBossRenderer(Context context) {
      super(context);
   }

   protected void scale(Cow entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      super.scale(entitylivingbaseIn, matrixStackIn, partialTickTime);
      matrixStackIn.scale(3.0F, 3.0F, 3.0F);
   }
}
