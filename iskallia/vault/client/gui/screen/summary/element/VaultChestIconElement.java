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
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class VaultChestIconElement<E extends VaultChestIconElement<E>> extends ContainerElement<E> {
   private static final int WIDTH = 111;
   private static final int HEIGHT = 53;

   public VaultChestIconElement(
      IPosition position,
      TextureAtlasRegion icon,
      Component name,
      int totalChests,
      int omegaChests,
      int epicChests,
      int rareChests,
      int commonChests,
      int trappedChests,
      VaultChestType vaultChestType
   ) {
      super(Spatials.positionXYZ(position).size(111, 53));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(111, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(111, 20))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(4, 20, 1).size(103, 33), ScreenTextures.VAULT_EXIT_ELEMENT_BG)
            .layout((screen, gui, parent, world) -> world.size(103, 33))
      );
      this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 4, 5), icon));
      Map<VaultChestType, Map<VaultRarity, Float>> map = ModConfigs.VAULT_STATS.getChests();
      float xpCommonMul = map.get(vaultChestType).get(VaultRarity.COMMON);
      float xpRareMul = map.get(vaultChestType).get(VaultRarity.RARE);
      float xpEpicMul = map.get(vaultChestType).get(VaultRarity.EPIC);
      float xpOmegaMul = map.get(vaultChestType).get(VaultRarity.OMEGA);
      float xpCommon = commonChests * xpCommonMul;
      float xpRare = rareChests * xpRareMul;
      float xpEpic = epicChests * xpEpicMul;
      float xpOmega = omegaChests * xpOmegaMul;
      float totalXp = xpCommon + xpRare + xpEpic + xpOmega;
      this.addElement(
         new VaultChestIconElement.StringElement(
            Spatials.positionXYZ(32, 8, 5), Spatials.size(16, 7), (Supplier<Component>)(() -> name), LabelTextStyle.shadow().left()
         )
      );
      int width = TextBorder.DEFAULT_FONT.get().width("Total: " + totalChests);
      this.addElement(
         new VaultChestIconElement.ValueElement(
            Spatials.positionXYZ(9, 26, 5), Spatials.size(width, 9), (Supplier<Integer>)(() -> totalChests), LabelTextStyle.shadow()
         )
      );
      Component xpComponent = new TextComponent(String.format("%.1fxp", totalXp)).withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR));
      this.addElement(
         new VaultChestIconElement.StringElement(
            Spatials.positionXYZ(100 - TextBorder.DEFAULT_FONT.get().width(xpComponent), 26, 5),
            Spatials.size(TextBorder.DEFAULT_FONT.get().width(xpComponent), 7),
            (Supplier<Component>)(() -> xpComponent),
            LabelTextStyle.shadow()
         )
      );
      this.addElement(
         (VaultChestIconElement.ChestTypeValueElement)new VaultChestIconElement.ChestTypeValueElement(
               Spatials.positionXYZ(15 - TextBorder.DEFAULT_FONT.get().width(String.valueOf(omegaChests)) / 2, 39, 5),
               Spatials.size(TextBorder.DEFAULT_FONT.get().width(String.valueOf(omegaChests)), 7),
               () -> omegaChests,
               LabelTextStyle.shadow(),
               VaultRarity.OMEGA.color
            )
            .tooltip(
               () -> new TextComponent("Omega")
                  .withStyle(VaultRarity.OMEGA.color)
                  .append(new TextComponent(": ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(String.format("%.1fxp", xpOmega)).withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR)))
            )
      );
      this.addElement(
         (VaultChestIconElement.ChestTypeValueElement)new VaultChestIconElement.ChestTypeValueElement(
               Spatials.positionXYZ(35 - TextBorder.DEFAULT_FONT.get().width(String.valueOf(epicChests)) / 2, 39, 5),
               Spatials.size(TextBorder.DEFAULT_FONT.get().width(String.valueOf(epicChests)), 7),
               () -> epicChests,
               LabelTextStyle.shadow(),
               VaultRarity.EPIC.color
            )
            .tooltip(
               () -> new TextComponent("Epic")
                  .withStyle(VaultRarity.EPIC.color)
                  .append(new TextComponent(": ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(String.format("%.1fxp", xpEpic)).withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR)))
            )
      );
      this.addElement(
         (VaultChestIconElement.ChestTypeValueElement)new VaultChestIconElement.ChestTypeValueElement(
               Spatials.positionXYZ(55 - TextBorder.DEFAULT_FONT.get().width(String.valueOf(rareChests)) / 2, 39, 5),
               Spatials.size(TextBorder.DEFAULT_FONT.get().width(String.valueOf(rareChests)), 7),
               () -> rareChests,
               LabelTextStyle.shadow(),
               VaultRarity.RARE.color
            )
            .tooltip(
               () -> new TextComponent("Rare")
                  .withStyle(VaultRarity.RARE.color)
                  .append(new TextComponent(": ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(String.format("%.1fxp", xpRare)).withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR)))
            )
      );
      this.addElement(
         (VaultChestIconElement.ChestTypeValueElement)new VaultChestIconElement.ChestTypeValueElement(
               Spatials.positionXYZ(75 - TextBorder.DEFAULT_FONT.get().width(String.valueOf(commonChests)) / 2, 39, 5),
               Spatials.size(TextBorder.DEFAULT_FONT.get().width(String.valueOf(commonChests)), 7),
               () -> commonChests,
               LabelTextStyle.shadow(),
               VaultRarity.COMMON.color
            )
            .tooltip(
               () -> new TextComponent("Common")
                  .withStyle(VaultRarity.COMMON.color)
                  .append(new TextComponent(": ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(String.format("%.1fxp", xpCommon)).withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR)))
            )
      );
      this.addElement(
         (VaultChestIconElement.ChestTypeValueElement)new VaultChestIconElement.ChestTypeValueElement(
               Spatials.positionXYZ(95 - TextBorder.DEFAULT_FONT.get().width(String.valueOf(trappedChests)) / 2, 39, 5),
               Spatials.size(TextBorder.DEFAULT_FONT.get().width(String.valueOf(trappedChests)), 7),
               () -> trappedChests,
               LabelTextStyle.shadow(),
               ChatFormatting.RED
            )
            .tooltip(() -> new TextComponent("Trapped").withStyle(ChatFormatting.RED))
      );
   }

   protected ScalableItemElement<?> makeElementSlot(ISpatial spatial, Supplier<ItemStack> itemStack, float scale) {
      return new ScalableItemElement(spatial, itemStack, scale);
   }

   private static final class ChestTypeValueElement extends DynamicLabelElement<Integer, VaultChestIconElement.ChestTypeValueElement> {
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

   private static final class StringElement extends DynamicLabelElement<Component, VaultChestIconElement.StringElement> {
      private StringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class ValueElement extends DynamicLabelElement<Integer, VaultChestIconElement.ValueElement> {
      private ValueElement(IPosition position, ISize size, Supplier<Integer> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Integer value) {
         this.set(new TextComponent("Total: " + value));
      }
   }

   public record ValueSupplier(Supplier<Integer> favorSupplier, Supplier<Component> tooltipTitleSupplier, Supplier<List<Component>> tooltipDescriptionSupplier) {
      public static VaultChestIconElement.ValueSupplier of(
         Supplier<Integer> favorSupplier, Supplier<Component> tooltipTitleSupplier, Supplier<List<Component>> tooltipDescriptionSupplier
      ) {
         return new VaultChestIconElement.ValueSupplier(favorSupplier, tooltipTitleSupplier, tooltipDescriptionSupplier);
      }
   }
}
