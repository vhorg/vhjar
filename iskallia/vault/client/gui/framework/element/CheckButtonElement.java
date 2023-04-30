package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.init.ModTextureAtlases;
import org.jetbrains.annotations.NotNull;

public class CheckButtonElement extends NineSliceButtonElement<CheckButtonElement> {
   public CheckButtonElement(ISpatial spatial, Runnable onClick) {
      super(spatial, ScreenTextures.BUTTON_EMPTY_TEXTURES, onClick);
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      renderer.render(
         TextureAtlasRegion.of(ModTextureAtlases.QUESTS, VaultMod.id("gui/quests/check")),
         poseStack,
         this.getWorldSpatial().x() + 2,
         this.getWorldSpatial().y() + 2,
         1
      );
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }
}
