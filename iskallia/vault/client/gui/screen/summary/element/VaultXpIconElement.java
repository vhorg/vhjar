package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.ScalableItemElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.summary.VaultEndScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class VaultXpIconElement<E extends VaultXpIconElement<E>> extends ContainerElement<E> {
   public VaultXpIconElement(IPosition position, TextureAtlasRegion icon, Component name, int width, int total, float xpAmount) {
      super(Spatials.positionXYZ(position).size(width, 24));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
      this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 4, 5), icon));
      this.addElement(
         new VaultXpIconElement.StringElement(
            Spatials.positionXYZ(32, 8, 5),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent(total + "x ").append(name)),
            LabelTextStyle.shadow().left()
         )
      );
      int textWidth = TextBorder.DEFAULT_FONT.get().width(new TextComponent(" " + xpAmount + "xp"));
      this.addElement(
         new VaultXpIconElement.StringElement(
            Spatials.positionXYZ(width - 4 - textWidth, 8, 5),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent(" " + xpAmount + "xp").withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR))),
            LabelTextStyle.shadow()
         )
      );
   }

   public VaultXpIconElement(IPosition position, ItemStack icon, Component name, int width, int total, float xpAmount) {
      super(Spatials.positionXYZ(position).size(width, 24));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
      this.addElement(new ScalableItemElement(Spatials.positionXYZ(8, 3, 5), () -> icon, 1.0F));
      this.addElement(
         new VaultXpIconElement.StringElement(
            Spatials.positionXYZ(32, 8, 5),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent(total + "x ").append(name)),
            LabelTextStyle.shadow().left()
         )
      );
      int textWidth = TextBorder.DEFAULT_FONT.get().width(new TextComponent(" " + xpAmount + "xp"));
      this.addElement(
         new VaultXpIconElement.StringElement(
            Spatials.positionXYZ(width - 4 - textWidth, 8, 5),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent(" " + xpAmount + "xp").withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR))),
            LabelTextStyle.shadow()
         )
      );
   }

   protected ScalableItemElement<?> makeElementSlot(ISpatial spatial, Supplier<ItemStack> itemStack, float scale) {
      return new ScalableItemElement(spatial, itemStack, scale);
   }

   private static final class ChestTypeValueElement extends DynamicLabelElement<Integer, VaultXpIconElement.ChestTypeValueElement> {
      Component trailingComponent;
      ChatFormatting chatFormatting;

      private ChestTypeValueElement(
         IPosition position,
         ISize size,
         Supplier<Integer> valueSupplier,
         LabelTextStyle.Builder labelTextStyle,
         Component trailingComponent,
         ChatFormatting chatFormatting
      ) {
         super(position, size, valueSupplier, labelTextStyle);
         this.chatFormatting = chatFormatting;
         this.trailingComponent = trailingComponent;
      }

      private ChestTypeValueElement(
         IPosition position, ISize size, Supplier<Integer> valueSupplier, LabelTextStyle.Builder labelTextStyle, ChatFormatting chatFormatting
      ) {
         super(position, size, valueSupplier, labelTextStyle);
         this.chatFormatting = chatFormatting;
         this.trailingComponent = new TextComponent("");
      }

      protected void onValueChanged(Integer value) {
         this.set(new TranslatableComponent(value.toString()).withStyle(this.chatFormatting).append(this.trailingComponent));
      }
   }

   private static final class StringElement extends DynamicLabelElement<Component, VaultXpIconElement.StringElement> {
      private StringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class ValueElement extends DynamicLabelElement<Integer, VaultXpIconElement.ValueElement> {
      private ValueElement(IPosition position, ISize size, Supplier<Integer> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Integer value) {
         this.set(new TextComponent("Total: " + value));
      }
   }

   public record ValueSupplier(Supplier<Integer> favorSupplier, Supplier<Component> tooltipTitleSupplier, Supplier<List<Component>> tooltipDescriptionSupplier) {
      public static VaultXpIconElement.ValueSupplier of(
         Supplier<Integer> favorSupplier, Supplier<Component> tooltipTitleSupplier, Supplier<List<Component>> tooltipDescriptionSupplier
      ) {
         return new VaultXpIconElement.ValueSupplier(favorSupplier, tooltipTitleSupplier, tooltipDescriptionSupplier);
      }
   }
}
