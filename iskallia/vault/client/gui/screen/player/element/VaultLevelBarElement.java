package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.HorizontalNumberedProgressBarElement;
import iskallia.vault.client.gui.framework.element.HorizontalProgressBarElement;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Supplier;
import net.minecraft.network.chat.TextColor;

public class VaultLevelBarElement<E extends VaultLevelBarElement<E>> extends HorizontalNumberedProgressBarElement<Integer, E> {
   public static final TextureAtlasRegion BACKGROUND = ScreenTextures.VAULT_LEVEL_BAR_BACKGROUND;
   public static final TextureAtlasRegion FOREGROUND = ScreenTextures.VAULT_LEVEL_BAR;
   public static final TextColor TEXT_COLOR = TextColor.parseColor("#FFE637");
   public static final TextColor BORDER_COLOR = TextColor.parseColor("#3E3E3E");

   public VaultLevelBarElement(IPosition position, Supplier<Float> percentageSupplier, Supplier<Integer> numberSupplier) {
      this(position, percentageSupplier, numberSupplier, HorizontalNumberedProgressBarElement.Style.CENTER);
   }

   public VaultLevelBarElement(
      IPosition position, Supplier<Float> percentageSupplier, Supplier<Integer> numberSupplier, HorizontalNumberedProgressBarElement.Style style
   ) {
      this(position, percentageSupplier, numberSupplier, style, IPosition.ZERO);
   }

   public VaultLevelBarElement(
      IPosition position,
      Supplier<Float> percentageSupplier,
      Supplier<Integer> numberSupplier,
      HorizontalNumberedProgressBarElement.Style style,
      IPosition numberOffset
   ) {
      super(
         position,
         BACKGROUND,
         FOREGROUND,
         percentageSupplier,
         HorizontalProgressBarElement.Direction.LEFT_TO_RIGHT,
         numberSupplier,
         String::valueOf,
         TEXT_COLOR,
         LabelTextStyle.border4(BORDER_COLOR),
         style,
         numberOffset
      );
   }
}
