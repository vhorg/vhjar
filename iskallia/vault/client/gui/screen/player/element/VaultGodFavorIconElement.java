package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class VaultGodFavorIconElement<E extends VaultGodFavorIconElement<E>> extends ContainerElement<E> {
   public VaultGodFavorIconElement(IPosition position, TextureAtlasRegion textureAtlasRegion, VaultGodFavorIconElement.ValueSupplier valueSupplier) {
      super(Spatials.positionXYZ(position).size(16, 16));
      this.addElement(new TextureAtlasElement(textureAtlasRegion));
      this.addElement(
         new VaultGodFavorIconElement.ValueElement(Spatials.positionY(9), Spatials.size(16, 7), valueSupplier.favorSupplier, LabelTextStyle.border4().right())
      );
      this.tooltip(
         Tooltips.shift(
            Tooltips.multi(() -> List.of(valueSupplier.tooltipTitleSupplier.get(), Tooltips.DEFAULT_HOLD_SHIFT_COMPONENT)),
            Tooltips.multi(valueSupplier.tooltipDescriptionSupplier)
         )
      );
   }

   private static final class ValueElement extends DynamicLabelElement<Integer, VaultGodFavorIconElement.ValueElement> {
      private ValueElement(IPosition position, ISize size, Supplier<Integer> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Integer value) {
         if (value > 0) {
            this.set(new TextComponent("+" + value));
         } else {
            this.set(new TextComponent(String.valueOf(value)));
         }
      }
   }

   public record ValueSupplier(Supplier<Integer> favorSupplier, Supplier<Component> tooltipTitleSupplier, Supplier<List<Component>> tooltipDescriptionSupplier) {
      public static VaultGodFavorIconElement.ValueSupplier of(
         Supplier<Integer> favorSupplier, Supplier<Component> tooltipTitleSupplier, Supplier<List<Component>> tooltipDescriptionSupplier
      ) {
         return new VaultGodFavorIconElement.ValueSupplier(favorSupplier, tooltipTitleSupplier, tooltipDescriptionSupplier);
      }
   }
}
