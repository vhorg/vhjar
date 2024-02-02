package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.summary.VaultExitContainerScreenData;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class LootStatsContainerElement extends VerticalScrollClipContainer<LootStatsContainerElement> {
   public static int VERTICAL_SPACING = 58;

   public LootStatsContainerElement(ISpatial spatial, VaultExitContainerScreenData screenData) {
      super(spatial, Padding.of(2, 0));
      this.addElement(new LootStatsContainerElement.VaultChestElement(Spatials.positionY(3), screenData).postLayout((screen, gui, parent, world) -> {
         world.translateX((this.innerWidth() - world.width()) / 2);
         return true;
      }));
   }

   public static int getOresMined(StatCollector statCollector) {
      int oresMined = 0;
      Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
      ObjectIterator var3 = group.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var3.next();
         ResourceLocation loc = (ResourceLocation)entry.getKey();
         int amount = entry.getIntValue();
         String path = loc.getPath();
         if (!path.equals("treasure_sand") && !path.equals("coin_pile")) {
            oresMined += amount;
         }
      }

      return oresMined;
   }

   private static final class ChestStringElement extends DynamicLabelElement<Component, LootStatsContainerElement.ChestStringElement> {
      private ChestStringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class VaultChestElement extends ElasticContainerElement<LootStatsContainerElement.VaultChestElement> {
      private VaultChestElement(IPosition position, VaultExitContainerScreenData screenData) {
         super(Spatials.positionXYZ(position));
         StatCollector statCollector = screenData.getStatsCollector();
         int woodenOmega = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.OMEGA);
         int woodenEpic = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.EPIC);
         int woodenRare = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.RARE);
         int woodenCommon = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.COMMON);
         int woodenTrapped = VaultExitContainerScreenData.getTrappedChestCount(statCollector, VaultChestType.WOODEN);
         int woodenTotal = woodenTrapped + woodenCommon + woodenRare + woodenEpic + woodenOmega;
         int gildedOmega = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.OMEGA);
         int gildedEpic = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.EPIC);
         int gildedRare = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.RARE);
         int gildedCommon = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.COMMON);
         int gildedTrapped = VaultExitContainerScreenData.getTrappedChestCount(statCollector, VaultChestType.GILDED);
         int gildedTotal = gildedTrapped + gildedCommon + gildedRare + gildedEpic + gildedOmega;
         int altarOmega = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.OMEGA);
         int altarEpic = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.EPIC);
         int altarRare = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.RARE);
         int altarCommon = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.COMMON);
         int altarTrapped = VaultExitContainerScreenData.getTrappedChestCount(statCollector, VaultChestType.ALTAR);
         int altarTotal = altarTrapped + altarCommon + altarRare + altarEpic + altarOmega;
         int livingOmega = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.OMEGA);
         int livingEpic = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.EPIC);
         int livingRare = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.RARE);
         int livingCommon = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.COMMON);
         int livingTrapped = VaultExitContainerScreenData.getTrappedChestCount(statCollector, VaultChestType.LIVING);
         int livingTotal = livingTrapped + livingCommon + livingRare + livingEpic + livingOmega;
         int ornateOmega = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.OMEGA);
         int ornateEpic = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.EPIC);
         int ornateRare = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.RARE);
         int ornateCommon = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.COMMON);
         int ornateTrapped = VaultExitContainerScreenData.getTrappedChestCount(statCollector, VaultChestType.ORNATE);
         int ornateTotal = ornateTrapped + ornateCommon + ornateRare + ornateEpic + ornateOmega;
         int treasureOmega = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.OMEGA);
         int treasureEpic = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.EPIC);
         int treasureRare = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.RARE);
         int treasureCommon = VaultExitContainerScreenData.getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.COMMON);
         int treasureTrapped = VaultExitContainerScreenData.getTrappedChestCount(statCollector, VaultChestType.TREASURE);
         int treasureTotal = treasureTrapped + treasureCommon + treasureRare + treasureEpic + treasureOmega;
         int totalOmega = woodenOmega + gildedOmega + altarOmega + livingOmega + ornateOmega + treasureOmega;
         int totalEpic = woodenEpic + gildedEpic + altarEpic + livingEpic + ornateEpic + treasureEpic;
         int totalRare = woodenRare + gildedRare + altarRare + livingRare + ornateRare + treasureRare;
         int totalCommon = woodenCommon + gildedCommon + altarCommon + livingCommon + ornateCommon + treasureCommon;
         int totalTrapped = woodenTrapped + gildedTrapped + altarTrapped + livingTrapped + ornateTrapped + treasureTrapped;
         int total = totalTrapped + totalCommon + totalRare + totalEpic + totalOmega;
         int treasureSand = 0;
         int coinPile = 0;
         Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
         ObjectIterator component = group.object2IntEntrySet().iterator();

         while (component.hasNext()) {
            Entry<ResourceLocation> entry = (Entry<ResourceLocation>)component.next();
            ResourceLocation loc = (ResourceLocation)entry.getKey();
            int amount = entry.getIntValue();
            String path = loc.getPath();
            if (path.equals("coin_pile")) {
               coinPile = amount;
            } else if (path.equals("treasure_sand")) {
               treasureSand = amount;
            }
         }

         Component componentx = new TextComponent("Total Chests Looted: ")
            .withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(String.valueOf(total)).withStyle(ChatFormatting.GREEN))
            .append(new TextComponent(", Trapped: ").withStyle(ChatFormatting.WHITE))
            .append(new TextComponent(String.valueOf(totalTrapped)).withStyle(ChatFormatting.RED));
         Component component2 = new TextComponent("Total Ores Mined: ")
            .withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(String.valueOf(LootStatsContainerElement.getOresMined(screenData.getStatsCollector()))).withStyle(ChatFormatting.GREEN));
         this.addElements(
            new LootStatsContainerElement.ChestStringElement(
               Spatials.positionXYZ(160, 4, 1), Spatials.size(16, 7), (Supplier<Component>)(() -> component), LabelTextStyle.shadow().center()
            ),
            new IElement[0]
         );
         Map<ResourceLocation, Float> map = ModConfigs.VAULT_STATS.getBlocksMined();
         float xpCoinPileMul = map.get(VaultMod.id("coin_pile"));
         float xpTreasureSandMul = map.get(VaultMod.id("treasure_sand"));
         int textWidthCoin = TextBorder.DEFAULT_FONT.get().width(new TextComponent("Coin Pile").append(" x" + coinPile))
            + TextBorder.DEFAULT_FONT.get().width(new TextComponent(" " + xpCoinPileMul * coinPile + "xp"));
         int textWidthSand = TextBorder.DEFAULT_FONT.get().width(new TextComponent("Treasure Sand").append(" x" + treasureSand))
            + TextBorder.DEFAULT_FONT.get().width(new TextComponent(" " + xpTreasureSandMul * treasureSand + "xp"));
         int bottomWidth = Math.max(textWidthCoin, textWidthSand);
         this.addElements(
            new VaultChestIconElement(
               Spatials.positionY(19).positionX(0),
               ScreenTextures.ICON_WOODEN_CHEST,
               new TextComponent("Wooden"),
               woodenTotal,
               woodenOmega,
               woodenEpic,
               woodenRare,
               woodenCommon,
               woodenTrapped,
               VaultChestType.WOODEN
            ),
            new IElement[]{
               new VaultChestIconElement(
                  Spatials.positionY(19).positionX(114),
                  ScreenTextures.ICON_LIVING_CHEST,
                  new TextComponent("Living"),
                  livingTotal,
                  livingOmega,
                  livingEpic,
                  livingRare,
                  livingCommon,
                  livingTrapped,
                  VaultChestType.LIVING
               ),
               new VaultChestIconElement(
                  Spatials.positionY(19).positionX(228),
                  ScreenTextures.ICON_GILDED_CHEST,
                  new TextComponent("Gilded"),
                  gildedTotal,
                  gildedOmega,
                  gildedEpic,
                  gildedRare,
                  gildedCommon,
                  gildedTrapped,
                  VaultChestType.GILDED
               ),
               new VaultChestIconElement(
                  Spatials.positionY(19 + LootStatsContainerElement.VERTICAL_SPACING).positionX(0),
                  ScreenTextures.ICON_ORNATE_CHEST,
                  new TextComponent("Ornate"),
                  ornateTotal,
                  ornateOmega,
                  ornateEpic,
                  ornateRare,
                  ornateCommon,
                  ornateTrapped,
                  VaultChestType.ORNATE
               ),
               new VaultChestIconElement(
                  Spatials.positionY(19 + LootStatsContainerElement.VERTICAL_SPACING).positionX(114),
                  ScreenTextures.ICON_ALTAR_CHEST,
                  new TextComponent("Altar"),
                  altarTotal,
                  altarOmega,
                  altarEpic,
                  altarRare,
                  altarCommon,
                  altarTrapped,
                  VaultChestType.ALTAR
               ),
               new VaultChestIconElement(
                  Spatials.positionY(19 + LootStatsContainerElement.VERTICAL_SPACING).positionX(228),
                  ScreenTextures.ICON_TREASURE_CHEST,
                  new TextComponent("Treasure"),
                  treasureTotal,
                  treasureOmega,
                  treasureEpic,
                  treasureRare,
                  treasureCommon,
                  treasureTrapped,
                  VaultChestType.TREASURE
               ),
               new VaultXpIconElement(
                  Spatials.positionY(38 + LootStatsContainerElement.VERTICAL_SPACING * 2).positionX(171 - (bottomWidth + 52) / 2),
                  ScreenTextures.ICON_COIN_STACKS,
                  new TextComponent("Coin Pile"),
                  bottomWidth + 52,
                  coinPile,
                  xpCoinPileMul * coinPile
               ),
               new VaultXpIconElement(
                  Spatials.positionY(66 + LootStatsContainerElement.VERTICAL_SPACING * 2).positionX(171 - (bottomWidth + 52) / 2),
                  new ItemStack(ModBlocks.TREASURE_SAND),
                  new TextComponent("Treasure Sand"),
                  bottomWidth + 52,
                  treasureSand,
                  xpTreasureSandMul * treasureSand
               )
            }
         );
         this.addElement(
            new TextureAtlasElement(
               Spatials.positionXYZ(10, 25 + LootStatsContainerElement.VERTICAL_SPACING * 2, 0).width(333),
               ScreenTextures.VAULT_EXIT_ELEMENT_HORIZONTAL_SPLITTER
            )
         );
         this.addElement(
            new TextureAtlasElement(
               Spatials.positionXYZ(10, 42 + LootStatsContainerElement.VERTICAL_SPACING * 3, 0).width(333),
               ScreenTextures.VAULT_EXIT_ELEMENT_HORIZONTAL_SPLITTER
            )
         );
         bottomWidth = 0;
         ObjectIterator elements = group.object2IntEntrySet().iterator();

         while (elements.hasNext()) {
            Entry<ResourceLocation> entry = (Entry<ResourceLocation>)elements.next();
            ResourceLocation loc = (ResourceLocation)entry.getKey();
            int amount = entry.getIntValue();
            if (!loc.getPath().equals("coin_pile") && !loc.getPath().equals("treasure_sand")) {
               ItemStack stack = new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(loc));
               float xpMul = map.get(loc);
               int textWidth = TextBorder.DEFAULT_FONT.get().width(new TextComponent(amount + "x ").append(stack.getHoverName()))
                  + TextBorder.DEFAULT_FONT.get().width(new TextComponent(" " + xpMul * amount + "xp"));
               bottomWidth = Math.max(bottomWidth, textWidth);
            }
         }

         int elementsx = 0;
         ObjectIterator var71 = group.object2IntEntrySet().iterator();

         while (var71.hasNext()) {
            Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var71.next();
            ResourceLocation loc = (ResourceLocation)entry.getKey();
            int amount = entry.getIntValue();
            if (amount != 0 && !loc.getPath().equals("coin_pile") && !loc.getPath().equals("treasure_sand")) {
               ItemStack stack = new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(loc));
               float xpMul = map.get(loc);
               this.addElements(
                  new VaultXpIconElement(
                     Spatials.positionY(55 + LootStatsContainerElement.VERTICAL_SPACING * 3 + 19 + 28 * elementsx).positionX(171 - (bottomWidth + 52) / 2),
                     stack,
                     stack.getHoverName(),
                     bottomWidth + 52,
                     amount,
                     xpMul * amount
                  ),
                  new IElement[0]
               );
               elementsx++;
            }
         }

         this.addElements(
            new LootStatsContainerElement.ChestStringElement(
               Spatials.positionXYZ(160, 42 + LootStatsContainerElement.VERTICAL_SPACING * 3 - 4 + 16, 1),
               Spatials.size(16, 7),
               (Supplier<Component>)(() -> component2),
               LabelTextStyle.shadow().center()
            ),
            new IElement[0]
         );
         this.addElement(
            new TextureAtlasElement(
               Spatials.positionXYZ(10, 42 + LootStatsContainerElement.VERTICAL_SPACING * 3 + 19 + 16 + 28 * elementsx, 0).width(333),
               ScreenTextures.VAULT_EXIT_ELEMENT_HORIZONTAL_SPLITTER
            )
         );
      }
   }
}
