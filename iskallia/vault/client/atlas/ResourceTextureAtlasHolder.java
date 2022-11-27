package iskallia.vault.client.atlas;

import iskallia.vault.VaultMod;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourceTextureAtlasHolder extends AbstractTextureAtlasHolder {
   protected final ResourceManager resourceManager;
   protected final ResourceLocation resourceLocation;

   public ResourceTextureAtlasHolder(
      TextureManager textureManager, ResourceManager resourceManager, ResourceLocation atlasResourceLocation, ResourceLocation resourceLocation
   ) {
      super(textureManager, atlasResourceLocation);
      this.resourceManager = resourceManager;
      this.resourceLocation = resourceLocation;
      textureManager.register(this.textureAtlas.location(), this.textureAtlas);
   }

   public ResourceTextureAtlasHolder(
      TextureManager textureManager,
      ResourceManager resourceManager,
      ResourceLocation atlasResourceLocation,
      ResourceLocation resourceLocation,
      Supplier<List<ResourceLocation>> validationSupplier
   ) {
      super(textureManager, atlasResourceLocation, validationSupplier);
      this.resourceManager = resourceManager;
      this.resourceLocation = resourceLocation;
      textureManager.register(this.textureAtlas.location(), this.textureAtlas);
   }

   @Nonnull
   @Override
   protected Stream<ResourceLocation> getResourcesToLoad() {
      try {
         return this.resourceManager
            .listResources(this.resourceLocation.getPath(), this::isValidResource)
            .stream()
            .filter(this::filterResourceLocation)
            .map(this::sanitizeResourceLocation);
      } catch (Exception var2) {
         VaultMod.LOGGER.error("Error gathering texture atlas resources", var2);
         return Stream.empty();
      }
   }

   protected boolean filterResourceLocation(ResourceLocation resourceLocation) {
      return this.resourceLocation.getNamespace().equals(resourceLocation.getNamespace());
   }

   protected boolean isValidResource(String resourceString) {
      return ResourceLocation.isValidResourceLocation(resourceString) && resourceString.endsWith(".png");
   }
}
