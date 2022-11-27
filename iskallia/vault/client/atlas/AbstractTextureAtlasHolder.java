package iskallia.vault.client.atlas;

import iskallia.vault.VaultMod;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureAtlas.Preparations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class AbstractTextureAtlasHolder extends SimplePreparableReloadListener<Preparations> implements AutoCloseable, ITextureAtlas {
   protected final TextureAtlas textureAtlas;
   protected final Supplier<List<ResourceLocation>> validationSupplier;

   public AbstractTextureAtlasHolder(TextureManager textureManager, ResourceLocation atlasResourceLocation) {
      this(textureManager, atlasResourceLocation, null);
   }

   public AbstractTextureAtlasHolder(TextureManager textureManager, ResourceLocation atlasResourceLocation, Supplier<List<ResourceLocation>> validationSupplier) {
      this.textureAtlas = new TextureAtlas(atlasResourceLocation);
      this.validationSupplier = validationSupplier;
      textureManager.register(this.textureAtlas.location(), this.textureAtlas);
   }

   @Nonnull
   protected Preparations prepare(@Nonnull ResourceManager resourceManager, ProfilerFiller profiler) {
      profiler.startTick();
      profiler.push("stitching");
      Preparations textureatlas$preparations = this.textureAtlas.prepareToStitch(resourceManager, this.getResourcesToLoad(), profiler, 0);
      profiler.pop();
      profiler.endTick();
      return textureatlas$preparations;
   }

   protected void apply(@Nonnull Preparations preparations, @Nonnull ResourceManager resourceManager, ProfilerFiller profiler) {
      profiler.startTick();
      profiler.push("upload");
      this.textureAtlas.reload(preparations);
      this.validateTextures();
      profiler.pop();
      profiler.endTick();
   }

   protected void validateTextures() {
      if (this.validationSupplier != null) {
         List<ResourceLocation> resourceLocationList = this.validationSupplier.get();

         for (ResourceLocation resourceLocation : resourceLocationList) {
            TextureAtlasSprite sprite = this.textureAtlas.getSprite(resourceLocation);
            if (sprite.getName() == MissingTextureAtlasSprite.getLocation()) {
               VaultMod.LOGGER.warn("Atlas is missing texture '%s'".formatted(resourceLocation));
            }
         }

         this.getResourcesToLoad().forEach(resourceLocationx -> {
            if (!resourceLocationList.contains(resourceLocationx)) {
               VaultMod.LOGGER.warn("Atlas has unused texture '%s'".formatted(resourceLocationx));
            }
         });
      }
   }

   @Override
   public void close() {
      this.textureAtlas.clearTextureData();
   }

   @Nonnull
   protected abstract Stream<ResourceLocation> getResourcesToLoad();

   @Override
   public ResourceLocation getAtlasResourceLocation() {
      return this.textureAtlas.location();
   }

   @Override
   public TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
      return this.textureAtlas.getSprite(resourceLocation);
   }

   @Nonnull
   protected ResourceLocation sanitizeResourceLocation(ResourceLocation resourceLocation) {
      return this.sanitizeResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath());
   }

   @Nonnull
   protected ResourceLocation sanitizeResourceLocation(String namespace, String path) {
      return new ResourceLocation(namespace, path.substring("textures/".length(), path.length() - 4));
   }
}
