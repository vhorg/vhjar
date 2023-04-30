package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.mushroom.SmolcapEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.SmolcapModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SmolcapRenderer extends MobRenderer<SmolcapEntity, SmolcapModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/mushroom/smolcap.png");

   public SmolcapRenderer(Context ctx) {
      super(ctx, new SmolcapModel(ctx.bakeLayer(ModModelLayers.SMOLCAP)), 0.3F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(SmolcapEntity entity) {
      return TEXTURE;
   }
}
