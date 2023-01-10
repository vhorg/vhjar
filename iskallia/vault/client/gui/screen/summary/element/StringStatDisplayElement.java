package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class StringStatDisplayElement<E extends StringStatDisplayElement<E>> extends ContainerElement<E> {
   public StringStatDisplayElement(
      IPosition position,
      TextureAtlasRegion icon,
      Component name,
      int width,
      int height,
      Map<String, String> stringMap,
      List<StatLabelListElement.Stat<?>> statList
   ) {
      super(Spatials.positionXYZ(position).size(width, Math.max(height, 31 + stringMap.size() * 16)));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(4, 20, 1).size(width - 8, height - 20), ScreenTextures.VAULT_EXIT_ELEMENT_BG)
            .layout((screen, gui, parent, world) -> world.size(width - 8, height - 20))
      );
      this.addElement(new TextureAtlasElement(Spatials.positionXYZ(8, 4, 5), icon));
      this.addElement(
         new StringStatDisplayElement.StringElement(
            Spatials.positionXYZ(32, 8, 5), Spatials.size(16, 7), (Supplier<Component>)(() -> name), LabelTextStyle.shadow().left()
         )
      );
      this.addElement(
         (StatLabelListElement)new StatLabelListElement(
               Spatials.positionY(27).positionX(8).positionZ(2).width(width - 16), TextColor.parseColor("#000000"), statList
            )
            .layout((screen, gui, parent, world) -> world.width(width - 16).height(statList.size() * 9))
      );
   }

   private static final class StringElement extends DynamicLabelElement<Component, StringStatDisplayElement.StringElement> {
      private StringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }
}
