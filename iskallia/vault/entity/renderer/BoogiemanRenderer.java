package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class BoogiemanRenderer extends ZombieRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/boogieman.png");

   public BoogiemanRenderer(Context renderManagerIn) {
      super(renderManagerIn);
      this.layers.remove(this.layers.size() - 1);
   }

   protected void scale(Zombie entitylivingbase, PoseStack matrixStack, float partialTickTime) {
      super.scale(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.scale(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation getTextureLocation(Zombie entity) {
      return TEXTURE;
   }
}
