package iskallia.vault.entity.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.wraith.VaultWraithEntity;
import iskallia.vault.entity.model.VaultWraithModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VaultWraithRenderer extends MobRenderer<VaultWraithEntity, VaultWraithModel> {
   private final ResourceLocation modelId;

   public VaultWraithRenderer(Context context, ModelLayerLocation modelLayerLocation) {
      super(context, new VaultWraithModel(context.bakeLayer(modelLayerLocation)), 0.25F);
      this.modelId = modelLayerLocation.getModel();
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull VaultWraithEntity entity) {
      String path = this.modelId.getPath();
      return VaultMod.id(String.format("textures/entity/wraith/%s.png", path));
   }
}
