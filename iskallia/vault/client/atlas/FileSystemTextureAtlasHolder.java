package iskallia.vault.client.atlas;

import iskallia.vault.VaultMod;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Deprecated
@OnlyIn(Dist.CLIENT)
public class FileSystemTextureAtlasHolder extends AbstractTextureAtlasHolder {
   protected final Path resourcePath;

   public FileSystemTextureAtlasHolder(TextureManager textureManager, ResourceLocation atlasResourceLocation, Path resourcePath) {
      super(textureManager, atlasResourceLocation);
      this.resourcePath = resourcePath;
   }

   @Nonnull
   @Override
   protected Stream<ResourceLocation> getResourcesToLoad() {
      Path rootPath = Paths.get("assets", "the_vault");
      Path assetPath = rootPath.resolve(this.resourcePath);
      List<ResourceLocation> result = new ArrayList<>();

      try {
         String rootPathString = rootPath.toString();
         URL rootPathURL = VaultMod.class.getClassLoader().getResource(rootPathString);
         String assetPathString = assetPath.toString();
         URL assetPathURL = VaultMod.class.getClassLoader().getResource(assetPathString);
         if (assetPathURL == null || rootPathURL == null) {
            return Stream.empty();
         }

         URI rootPathURI = rootPathURL.toURI();
         URI assetPathURI = assetPathURL.toURI();

         try (FileSystem fileSystem = assetPathURI.getScheme().equals("jar") ? FileSystems.newFileSystem(assetPathURI, Collections.emptyMap()) : null) {
            Path rootPathFS = fileSystem == null ? Paths.get(rootPathURI) : fileSystem.getPath(rootPathString);
            Path assetPathFS = fileSystem == null ? Paths.get(assetPathURI) : fileSystem.getPath(assetPathString);
            Files.walkFileTree(assetPathFS, new FileSystemTextureAtlasHolder.Visitor(rootPathFS, this::sanitizeResourceLocation, result));
         } catch (IOException var15) {
            VaultMod.LOGGER.error("Error gathering texture atlas resources", var15);
         }
      } catch (URISyntaxException var16) {
         VaultMod.LOGGER.error("Error gathering texture atlas resources", var16);
      }

      return result.stream();
   }

   private static class Visitor extends SimpleFileVisitor<Path> {
      private final Path rootPath;
      private final List<ResourceLocation> result;
      private final BiFunction<String, String, ResourceLocation> sanitizer;

      public Visitor(Path rootPath, BiFunction<String, String, ResourceLocation> sanitizer, List<ResourceLocation> result) {
         this.rootPath = rootPath;
         this.result = result;
         this.sanitizer = sanitizer;
      }

      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
         String resourcePathString = this.rootPath.relativize(file).toString();
         this.result.add(this.sanitizer.apply("the_vault", resourcePathString));
         return FileVisitResult.CONTINUE;
      }
   }
}
