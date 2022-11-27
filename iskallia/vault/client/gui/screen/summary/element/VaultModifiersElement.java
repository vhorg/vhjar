package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.VaultMod;
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
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class VaultModifiersElement<E extends VaultModifiersElement<E>> extends ContainerElement<E> {
   public VaultModifiersElement(IPosition position, TextureAtlasRegion icon, int width, int height, Component name, Map<VaultModifier<?>, Integer> supplier) {
      super(Spatials.positionXYZ(position).size(width, height + supplier.size() * 18 + 6));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
      if (supplier.size() > 0) {
         this.addElement(
            (NineSliceElement)new NineSliceElement(
                  Spatials.positionXYZ(4, 20, 1).size(width - 8, height - 20 + supplier.size() * 18), ScreenTextures.VAULT_EXIT_ELEMENT_BG
               )
               .layout((screen, gui, parent, world) -> world.size(width - 8, height - 20 + supplier.size() * 18))
         );
      }

      this.addElement(new TextureAtlasElement(Spatials.positionXYZ(8, 4, 5), icon));
      this.addElement(
         new VaultModifiersElement.ChestStringElement(
            Spatials.positionXYZ(32, 8, 4), Spatials.size(16, 7), (Supplier<Component>)(() -> name), LabelTextStyle.shadow().left()
         )
      );
      AtomicInteger iterator = new AtomicInteger();
      supplier.forEach(
         (vaultModifier, integer) -> {
            int textWidth = TextBorder.DEFAULT_FONT.get().width(vaultModifier.getDisplayNameFormatted(integer));
            int textWidth2 = TextBorder.DEFAULT_FONT.get().width("x" + integer);
            this.addElement(
               (VaultModifiersElement.ValueElement)new VaultModifiersElement.ValueElement(
                     Spatials.positionXYZ(30, 30 + iterator.get() * 18, 2),
                     Spatials.size(textWidth, 9),
                     vaultModifier.getDisplayName(),
                     LabelTextStyle.shadow()
                  )
                  .tooltip(() -> new TextComponent(vaultModifier.getDisplayDescriptionFormatted(integer)))
            );
            this.addElement(
               (VaultModifiersElement.ValueElement)new VaultModifiersElement.ValueElement(
                     Spatials.positionXYZ(width - 12 - textWidth2, 30 + iterator.get() * 18, 2),
                     Spatials.size(textWidth2, 9),
                     "x" + integer,
                     LabelTextStyle.shadow()
                  )
                  .tooltip(() -> new TextComponent(vaultModifier.getDisplayDescriptionFormatted(integer)))
            );
            Optional<ResourceLocation> icon_loc = vaultModifier.getIcon();
            icon_loc.ifPresent(
               resourceLocation -> this.addElement(
                     new TextureAtlasElement(
                        Spatials.positionXYZ(10, 26 + iterator.get() * 18, 2).size(16, 16),
                        TextureAtlasRegion.of(ModTextureAtlases.MODIFIERS, VaultMod.id(resourceLocation.getPath()))
                     )
                  )
                  .tooltip(() -> new TextComponent(vaultModifier.getDisplayDescriptionFormatted(integer)))
            );
            iterator.getAndIncrement();
         }
      );
   }

   private static final class ChestStringElement extends DynamicLabelElement<Component, VaultModifiersElement.ChestStringElement> {
      private ChestStringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class ValueElement extends DynamicLabelElement<String, VaultModifiersElement.ValueElement> {
      private ValueElement(IPosition position, ISize size, String string, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, () -> string, labelTextStyle);
      }

      protected void onValueChanged(String value) {
         this.set(new TextComponent(value));
      }
   }
}
