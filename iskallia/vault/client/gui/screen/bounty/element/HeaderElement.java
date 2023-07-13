package iskallia.vault.client.gui.screen.bounty.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeaderElement extends ContainerElement<HeaderElement> {
   private final NineSliceElement<?> titleBackground;
   private final LabelElement<?> titleText;
   private NineSliceElement<?> iconBackground;
   private TextureAtlasElement<?> iconElement;
   private Component title;
   private Component lastTitle;

   public HeaderElement(ISpatial spatial, Component title, boolean contentBackgroundVisible) {
      this(spatial, title, null, contentBackgroundVisible, LabelTextStyle.shadow());
   }

   public HeaderElement(ISpatial spatial, Component title, TextureAtlasRegion icon, boolean contentBackgroundVisible) {
      this(spatial, title, icon, contentBackgroundVisible, LabelTextStyle.shadow());
   }

   public HeaderElement(ISpatial spatial, Component title, LabelTextStyle.Builder labelTextStyle) {
      this(spatial, title, null, false, labelTextStyle);
   }

   public HeaderElement(ISpatial spatial, Component title) {
      this(spatial, title, null, false, LabelTextStyle.shadow());
   }

   public HeaderElement(
      ISpatial spatial, Component title, @Nullable TextureAtlasRegion icon, boolean contentBackgroundVisible, LabelTextStyle.Builder labelTextStyle
   ) {
      super(spatial);
      this.title = title;
      this.titleBackground = this.addElement(
         new NineSliceElement(Spatials.positionXYZ(0, 0, 10).size(spatial.width(), 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
      );
      int x = labelTextStyle.build().textAlign() == TextAlign.CENTER ? 0 : 3;
      this.titleText = this.addElement(
         new LabelElement(Spatials.positionXYZ(icon == null ? x : 32, 6, 11).size(spatial.width(), 9), title, labelTextStyle).layout(this.adjustLabelLayout())
      );
      if (icon != null) {
         this.iconBackground = this.addElement(new NineSliceElement(Spatials.positionXYZ(5, -2, 15).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON));
         this.iconElement = this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 2, 16), icon));
      }

      if (contentBackgroundVisible) {
         NineSliceElement var7 = this.addElement(
            new NineSliceElement(
               Spatials.positionXYZ(2, this.titleBackground.bottom() - 2, 0).size(spatial.width() - 4, spatial.height() + 8),
               ScreenTextures.VAULT_EXIT_ELEMENT_BG
            )
         );
      }
   }

   private void updateTitle() {
      this.titleText.set(this.title);
      this.lastTitle = this.title;
   }

   public void setTitle(MutableComponent component) {
      this.title = component;
   }

   public void setIcon(TextureAtlasRegion textureAtlasRegion) {
      this.iconBackground = this.addElement(new NineSliceElement(Spatials.positionXYZ(5, -2, 15).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON));
      this.iconElement = this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 1, 16), textureAtlasRegion));
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      if (!this.title.equals(this.lastTitle)) {
         this.updateTitle();
      }

      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }

   @NotNull
   public ILayoutStrategy adjustLabelLayout() {
      return (screen, gui, parent, world) -> world.size(this.width(), this.height());
   }
}
