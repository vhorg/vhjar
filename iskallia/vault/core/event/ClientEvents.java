package iskallia.vault.core.event;

import iskallia.vault.core.event.client.AmbientLightEvent;
import iskallia.vault.core.event.client.AmbientParticlesEvent;
import iskallia.vault.core.event.client.BiomeColorsEvent;
import iskallia.vault.core.event.client.ClientTickEvent;
import iskallia.vault.core.event.client.DimensionEffectEvent;
import iskallia.vault.core.event.client.FogColorsEvent;
import iskallia.vault.core.event.client.MouseScrollEvent;
import iskallia.vault.core.event.client.RenderLevelLastEvent;
import iskallia.vault.core.event.client.RenderOverlayEvent;
import iskallia.vault.core.event.client.UpdateLightEvent;
import iskallia.vault.core.event.common.CompassPropertyEvent;
import java.util.ArrayList;
import java.util.List;

public class ClientEvents {
   private static final List<Event<?, ?>> REGISTRY = new ArrayList<>();
   public static final ClientTickEvent CLIENT_TICK = register(new ClientTickEvent());
   public static final RenderOverlayEvent RENDER_OVERLAY = register(new RenderOverlayEvent());
   public static final AmbientLightEvent AMBIENT_LIGHT = register(new AmbientLightEvent());
   public static final BiomeColorsEvent BIOME_COLORS = register(new BiomeColorsEvent());
   public static final AmbientParticlesEvent AMBIENT_PARTICLE = register(new AmbientParticlesEvent());
   public static final FogColorsEvent FOG_COLORS = register(new FogColorsEvent());
   public static final UpdateLightEvent UPDATE_LIGHT = register(new UpdateLightEvent());
   public static final DimensionEffectEvent WORLD_EFFECT = register(new DimensionEffectEvent());
   public static final RenderLevelLastEvent RENDER_LEVEL_LAST = register(new RenderLevelLastEvent());
   public static final MouseScrollEvent MOUSE_SCROLL = register(new MouseScrollEvent());
   public static final CompassPropertyEvent COMPASS_PROPERTY = register(new CompassPropertyEvent());

   public static void release(Object reference) {
      REGISTRY.forEach(event -> event.release(reference));
   }

   private static <T extends Event<?, ?>> T register(T event) {
      REGISTRY.add(event);
      return event;
   }
}
