package iskallia.vault.client.gui.screen.bounty.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeaderElement extends ContainerElement<HeaderElement> {
   private final NineSliceElement<?> titleBackground;
   private final LabelElement<?> titleText;
   private NineSliceElement<?> iconBackground;
   private TextureAtlasElement<?> iconElement;
   private MutableComponent title;
   private MutableComponent lastTitle;

   public HeaderElement(ISpatial spatial, TextComponent title, boolean contentBackgroundVisible) {
      this(spatial, title, null, contentBackgroundVisible);
   }

   public HeaderElement(ISpatial spatial, TextComponent title) {
      this(spatial, title, null, false);
   }

   public HeaderElement(ISpatial spatial, TextComponent title, @Nullable TextureAtlasRegion icon, boolean contentBackgroundVisible) {
      super(spatial);
      this.title = title;
      this.titleBackground = this.addElement(
         new NineSliceElement(Spatials.positionXYZ(0, 0, 10).size(spatial.width(), 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
      );
      this.titleText = this.addElement(
         new LabelElement(Spatials.positionXYZ(icon == null ? 3 : 32, 6, 11).size(spatial.width(), 9), title, LabelTextStyle.shadow())
      );
      if (icon != null) {
         this.iconBackground = this.addElement(new NineSliceElement(Spatials.positionXYZ(5, -2, 15).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON));
         this.iconElement = this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 1, 16), icon));
      }

      if (contentBackgroundVisible) {
         NineSliceElement var5 = this.addElement(
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

   public void setIcon(ResourceLocation taskType) {
      this.iconElement = this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 1, 16), BountyScreen.TASK_ICON_MAP.get(taskType)));
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      if (!this.title.equals(this.lastTitle)) {
         this.updateTitle();
      }

      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }
}
