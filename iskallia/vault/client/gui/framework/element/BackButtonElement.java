package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

public class BackButtonElement extends NineSliceButtonElement<BackButtonElement> {
   public BackButtonElement(ISpatial spatial, AbstractElementScreen previousScreen) {
      super(spatial, ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> Minecraft.getInstance().setScreen(previousScreen));
      this.label(() -> new TranslatableComponent("gui.back"), LabelTextStyle.shadow().center());
   }
}
