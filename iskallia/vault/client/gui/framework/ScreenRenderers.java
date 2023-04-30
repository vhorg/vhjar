package iskallia.vault.client.gui.framework;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.atlas.AtlasMultiBuffer;
import iskallia.vault.client.gui.framework.render.ClipRegionStrategies;
import iskallia.vault.client.gui.framework.render.DebugRenderer;
import iskallia.vault.client.gui.framework.render.ElementRenderers;
import iskallia.vault.client.gui.framework.render.spi.IDebugRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.init.ModTextureAtlases;
import net.minecraftforge.common.util.LazyOptional;

public final class ScreenRenderers {
   private static final LazyOptional<AtlasMultiBuffer> BUFFERED = LazyOptional.of(
      () -> AtlasMultiBuffer.builder()
         .add(ModTextureAtlases.SCREEN, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .add(ModTextureAtlases.SCAVENGER, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .add(ModTextureAtlases.MODIFIERS, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .add(ModTextureAtlases.MOB_HEADS, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .add(ModTextureAtlases.QUESTS, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .add(ModTextureAtlases.ABILITIES, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .add(ModTextureAtlases.SKILLS, Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
         .create()
   );
   private static final LazyOptional<BufferBuilder> DEBUG_BUFFERED = LazyOptional.of(() -> new BufferBuilder(256));

   public static IElementRenderer getBuffered() {
      return (IElementRenderer)BUFFERED.map(buffer -> ElementRenderers.bufferedPosTex(buffer, ClipRegionStrategies.STENCIL))
         .orElseThrow(() -> new RuntimeException("Error creating buffered screen renderer"));
   }

   public static IElementRenderer getImmediate() {
      return ElementRenderers.immediate(ClipRegionStrategies.STENCIL);
   }

   public static IDebugRenderer getDebugNone() {
      return IDebugRenderer.NONE;
   }

   public static IDebugRenderer getDebugBuffered() {
      return (IDebugRenderer)DEBUG_BUFFERED.map(DebugRenderer::new).orElseThrow(() -> new RuntimeException("Error creating debug buffer"));
   }

   private ScreenRenderers() {
   }
}
