package iskallia.vault.entity.renderer.elite;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EndervexEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EndervexModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EndervexRenderer extends MobRenderer<EndervexEntity, EndervexModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/enderman_ornament.png");

   public EndervexRenderer(Context context) {
      super(context, new EndervexModel(context.bakeLayer(ModModelLayers.ENDERVEX)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull EndervexEntity pEntity) {
      return TEXTURE_LOCATION;
   }
}
