package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;

public class MonsterEyeRenderer extends SlimeRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/monster_eye.png");

   public MonsterEyeRenderer(Context context) {
      super(context);
   }

   protected void scale(Slime entitylivingbase, PoseStack matrixStack, float partialTickTime) {
      super.scale(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.scale(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation getTextureLocation(Slime entity) {
      return TEXTURE;
   }
}
