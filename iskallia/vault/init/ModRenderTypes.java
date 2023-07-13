package iskallia.vault.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.VaultMod;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public final class ModRenderTypes extends RenderType {
   public static final RenderType TAUNT_CHARM_INDICATOR = RenderType.create(
      "taunt_charm_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/mob_effect/taunt_charm_indicator.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType TAUNT_FEAR_INDICATOR = RenderType.create(
      "taunt_fear_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/mob_effect/taunt_repel_player.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType VULNERABLE_INDICATOR = RenderType.create(
      "vulnerable_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/mob_effect/vulnerable.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType CHAMPION_BUFF_FG_INDICATOR = RenderType.create(
      "champion_buff_fg_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/mob_effect/champion_buff_fg.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType CHAMPION_BUFF_BG_INDICATOR = RenderType.create(
      "champion_buff_bg_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/mob_effect/champion_buff_bg.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType GLACIAL_SHATTER_INDICATOR = RenderType.create(
      "glacial_shatter_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/mob_effect/glacial_shatter.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType CHAMPION_INDICATOR = RenderType.create(
      "champion_indicator",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(VaultMod.id("textures/entity/champion_indicator.png"), false, false))
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType TOTEM_GLYPH_EFFECT = RenderType.create(
      "totem_glyph_effect",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(new ResourceLocation("textures/font/asciillager.png"), false, false))
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );
   public static final RenderType TOTEM_LASER_EFFECT = RenderType.create(
      "totem_laser_effect",
      DefaultVertexFormat.POSITION_COLOR,
      Mode.QUADS,
      1024,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
         .setTextureState(RenderStateShard.NO_TEXTURE)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
         .setCullState(RenderStateShard.NO_CULL)
         .createCompositeState(true)
   );
   public static final RenderType TOTEM_GLOW_LAYER = RenderType.create(
      "totem_glow_layer",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
         .setCullState(RenderStateShard.CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .createCompositeState(false)
   );

   private ModRenderTypes(
      String pName,
      VertexFormat pFormat,
      Mode pMode,
      int pBufferSize,
      boolean pAffectsCrumbling,
      boolean pSortOnUpload,
      Runnable pSetupState,
      Runnable pClearState
   ) {
      super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
   }
}
