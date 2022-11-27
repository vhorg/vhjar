package iskallia.vault.client.gui.screen.player.legacy.widget;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.init.ModTextureAtlases;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ArchetypeNodeTextures {
   private static final Set<ResourceLocation> RESOURCE_LOCATIONS = new HashSet<>();
   public static final Map<NodeState, TextureAtlasRegion> NODE = Map.of(
      NodeState.DEFAULT,
      createTextureAtlasRegion("gui/archetypes/node"),
      NodeState.HOVERED,
      createTextureAtlasRegion("gui/archetypes/node_green"),
      NodeState.SELECTED,
      createTextureAtlasRegion("gui/archetypes/node_yellow"),
      NodeState.DISABLED,
      createTextureAtlasRegion("gui/archetypes/node_dark")
   );
   public static final Map<NodeState, TextureAtlasRegion> NODE_SMALL = Map.of(
      NodeState.DEFAULT,
      createTextureAtlasRegion("gui/archetypes/node_small"),
      NodeState.HOVERED,
      createTextureAtlasRegion("gui/archetypes/node_small_green"),
      NodeState.SELECTED,
      createTextureAtlasRegion("gui/archetypes/node_small_yellow"),
      NodeState.DISABLED,
      createTextureAtlasRegion("gui/archetypes/node_small_dark")
   );
   public static final TextureAtlasRegion BANNER = createTextureAtlasRegion("gui/archetypes/banner");

   @NotNull
   private static TextureAtlasRegion createTextureAtlasRegion(String name) {
      ResourceLocation resourceLocation = VaultMod.id(name);
      RESOURCE_LOCATIONS.add(resourceLocation);
      return TextureAtlasRegion.of(ModTextureAtlases.ARCHETYPES, resourceLocation);
   }

   public static Stream<ResourceLocation> stream() {
      return Stream.concat(RESOURCE_LOCATIONS.stream(), Stream.of(BANNER.resourceLocation()));
   }

   private ArchetypeNodeTextures() {
   }
}
