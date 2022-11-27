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

public class AbilityNodeTextures {
   private static final Set<ResourceLocation> RESOURCE_LOCATIONS = new HashSet<>();
   public static final Map<NodeState, TextureAtlasRegion> PRIMARY_NODE = Map.of(
      NodeState.DEFAULT,
      createTextureAtlasRegion("gui/abilities/node_primary"),
      NodeState.HOVERED,
      createTextureAtlasRegion("gui/abilities/node_primary_green"),
      NodeState.SELECTED,
      createTextureAtlasRegion("gui/abilities/node_primary_yellow"),
      NodeState.DISABLED,
      createTextureAtlasRegion("gui/abilities/node_primary_dark")
   );
   public static final Map<NodeState, TextureAtlasRegion> SECONDARY_NODE = Map.of(
      NodeState.DEFAULT,
      createTextureAtlasRegion("gui/abilities/node_secondary"),
      NodeState.HOVERED,
      createTextureAtlasRegion("gui/abilities/node_secondary_green"),
      NodeState.SELECTED,
      createTextureAtlasRegion("gui/abilities/node_secondary_yellow"),
      NodeState.DISABLED,
      createTextureAtlasRegion("gui/abilities/node_secondary_dark")
   );
   public static final TextureAtlasRegion NODE_BACKGROUND_LEVEL = createTextureAtlasRegion("gui/abilities/node_background_level");

   @NotNull
   private static TextureAtlasRegion createTextureAtlasRegion(String name) {
      ResourceLocation resourceLocation = VaultMod.id(name);
      RESOURCE_LOCATIONS.add(resourceLocation);
      return TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, resourceLocation);
   }

   public static Stream<ResourceLocation> stream() {
      return RESOURCE_LOCATIONS.stream();
   }

   private AbilityNodeTextures() {
   }
}
