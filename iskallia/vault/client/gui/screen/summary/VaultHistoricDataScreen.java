package iskallia.vault.client.gui.screen.summary;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientHistoricFavoritesData;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.RenderIndexedElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.TitleElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.summary.element.StatLabelListElement;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundAddHistoricFavoriteMessage;
import iskallia.vault.network.message.ServerboundSendSnapshotLinkMessage;
import iskallia.vault.util.function.ObservableSupplier;
import iskallia.vault.world.data.VaultSnapshots;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VaultHistoricDataScreen extends AbstractElementScreen {
   public static final int TITLE_WIDTH = 128;
   public static final int BUTTON_WIDTH = 52;
   public static final int BUTTON_HEIGHT = 19;
   protected ButtonElement<?> closeButton;
   private static final boolean DEBUG = false;
   public static final TextColor XP_COLOR = TextColor.parseColor("#FFE637");
   private final List<VaultSnapshot> snapshots;
   private final List<VaultSnapshot> prev50;
   private VaultHistoricDataScreen.HistoricContainerElement historyContainer;
   private VaultHistoricDataScreen.HistoricContainerElement favoritesContainer;
   private LabelElement<?> historicLabel;
   private LabelElement<?> favoritesLabel;
   private VaultHistoricDataScreen.VaultHistoricDataTabElement tabs;

   public VaultHistoricDataScreen(List<VaultSnapshot> snapshots, Component title) {
      super(title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.snapshots = new ArrayList<>(snapshots);
      Collections.reverse(this.snapshots);
      this.setGuiSize(Spatials.size(350, 186));
      if (Minecraft.getInstance().player != null) {
         this.prev50 = VaultSnapshots.getPrev50(Minecraft.getInstance().player.getUUID(), snapshots);
      } else {
         this.prev50 = new ArrayList<>();
      }

      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionY(-4).positionZ(-12).positionY(6), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.width(128).height(32).translateX((gui.right() - gui.left()) / 2 + gui.left() - 64))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(
               Spatials.positionXY(0, 42).size(this.width, 19).height(this.getTabContentSpatial()), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
            )
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 7).size(gui.width() + 16 + 26, this.getTabContentSpatial().height()))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionY(-4).positionZ(-10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.x() - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .size(gui.width() + 26, gui.height() - 10)
            )
      );
      this.historyContainer = this.addElement(
         new VaultHistoricDataScreen.HistoricContainerElement(Spatials.positionX(4).width(-7).height(-16), this.prev50, false)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      this.favoritesContainer = this.addElement(
         new VaultHistoricDataScreen.HistoricContainerElement(Spatials.positionX(4).width(-7).height(-16), snapshots, true)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      this.historicLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Previous 50 Vaults").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      this.favoritesLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Favorites").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      TitleElement<?> titleHistory = this.addElement(
         new TitleElement(Spatials.positionY(16), new TextComponent("History").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout(
               (screen, gui, parent, world) -> world.translateX(
                  (gui.right() - gui.left()) / 2 + gui.left() - TextBorder.DEFAULT_FONT.get().width(new TextComponent("History"))
               )
            )
      );
      TitleElement<?> titleFavorites = this.addElement(
         new TitleElement(Spatials.positionY(16), new TextComponent("Favorites").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout(
               (screen, gui, parent, world) -> world.translateX(
                  (gui.right() - gui.left()) / 2 + gui.left() - TextBorder.DEFAULT_FONT.get().width(new TextComponent("Favorites"))
               )
            )
      );
      this.tabs = this.addElement(
         (VaultHistoricDataScreen.VaultHistoricDataTabElement)new VaultHistoricDataScreen.VaultHistoricDataTabElement(Spatials.positionXY(-3, 3), index -> {
            this.historyContainer.setEnabled(index == 0);
            this.historyContainer.setVisible(index == 0);
            this.historicLabel.setEnabled(index == 0);
            this.historicLabel.setVisible(index == 0);
            titleHistory.setVisible(index == 0);
            titleHistory.setEnabled(index == 0);
            this.favoritesContainer.setEnabled(index == 1);
            this.favoritesContainer.setVisible(index == 1);
            this.favoritesLabel.setEnabled(index == 1);
            this.favoritesLabel.setVisible(index == 1);
            titleFavorites.setVisible(index == 1);
            titleFavorites.setEnabled(index == 1);
         }, false).layout((screen, gui, parent, world) -> world.translateX(gui.right() + 7).translateY(this.getTabContentSpatial().bottom()))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.zero(), new TextComponent("Close").withStyle(ChatFormatting.WHITE), LabelTextStyle.border4(ChatFormatting.BLACK).center()
            )
            .layout(
               (screen, gui, parent, world) -> world.translateZ(2)
                  .translateX(gui.right() - gui.left() + gui.left() - 26 - 1 - TextBorder.DEFAULT_FONT.get().width("Close") / 2)
                  .translateY(this.getTabContentSpatial().bottom() + gui.height() - 31)
            )
      );
      this.closeButton = this.addElement(
         new ButtonElement<ButtonElement<ButtonElement<?>>>(Spatials.zero(), ScreenTextures.BUTTON_CLOSE_TEXTURES, () -> {
               this.onClose();
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            })
            .layout(
               (screen, gui, parent, world) -> world.width(52)
                  .height(19)
                  .translateX(gui.right() - gui.left() + gui.left() - 52)
                  .translateY(this.getTabContentSpatial().bottom() + gui.height() - 37)
            )
            .tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               tooltipRenderer.renderTooltip(poseStack, List.of(new TextComponent("Close")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
               return false;
            })
      );
   }

   public VaultHistoricDataScreen.HistoricContainerElement getFavoritesContainer() {
      return this.favoritesContainer;
   }

   @Override
   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   public void onClose() {
      super.onClose();
   }

   public ISpatial getTabContentSpatial() {
      int padLeft = 21;
      int padTop = 42;
      int width = this.width - padLeft * 2;
      int height = 19;
      return Spatials.positionXY(padLeft, padTop).size(width, height);
   }

   @Override
   protected void layout(ISpatial parent) {
      super.layout(parent);
   }

   public class FavoritesDisplayElement<E extends VaultHistoricDataScreen.FavoritesDisplayElement<E>> extends ContainerElement<E> {
      private UUID vaultID;
      private VaultSnapshot snapshot;

      public FavoritesDisplayElement(
         IPosition position,
         TextureAtlasRegion icon,
         TextureAtlasRegion iconGray,
         Component name,
         int width,
         int height,
         Map<String, String> stringMap,
         List<StatLabelListElement.Stat<?>> statList,
         VaultExitContainerScreenData screenData
      ) {
         super(Spatials.positionXYZ(position).size(width, Math.max(height, 31 + stringMap.size() * 16)));
         this.vaultID = screenData.getSnapshot().getEnd().get(Vault.ID);
         this.snapshot = screenData.getSnapshot();
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
         this.<VaultHistoricDataScreen.GrayedTextureAtlasElement>addElement(
               new VaultHistoricDataScreen.GrayedTextureAtlasElement(Spatials.positionXYZ(8, 4, 5), icon, iconGray, this.vaultID, () -> {
                  ModNetwork.CHANNEL.sendToServer(new ServerboundAddHistoricFavoriteMessage(this.vaultID));
                  if (Minecraft.getInstance().screen instanceof VaultHistoricDataScreen vaultHistoricDataScreen) {
                     VaultHistoricDataScreen.this.favoritesContainer.removeElements();
                     VaultHistoricDataScreen.this.favoritesContainer.reset(true);
                  }
               })
            )
            .tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               boolean favorite = false;
               List<UUID> list = ClientHistoricFavoritesData.getFavorites();
               if (list != null) {
                  for (UUID value : list) {
                     if (value.equals(this.vaultID)) {
                        favorite = true;
                        break;
                     }
                  }
               }

               if (!favorite) {
                  tooltipRenderer.renderTooltip(poseStack, List.of(new TextComponent("Favorite")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
               } else {
                  tooltipRenderer.renderTooltip(poseStack, List.of(new TextComponent("Un-Favorite")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
               }

               return false;
            });
         this.addElement(
            new VaultHistoricDataScreen.FavoritesDisplayElement.StringElement(
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

      private static final class StringElement extends DynamicLabelElement<Component, VaultHistoricDataScreen.FavoritesDisplayElement.StringElement> {
         private StringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
            super(position, size, valueSupplier, labelTextStyle);
         }

         protected void onValueChanged(Component value) {
            this.set(value);
         }
      }
   }

   public static class GrayedTextureAtlasElement<E extends TextureAtlasElement<E>>
      extends AbstractSpatialElement<E>
      implements IRenderedElement,
      IGuiEventElement {
      private final TextureAtlasRegion textureAtlasRegion;
      private final TextureAtlasRegion grayTextureAtlasRegion;
      private final UUID vaultID;
      private boolean visible;
      private final Runnable onClick;

      public GrayedTextureAtlasElement(
         IPosition position, TextureAtlasRegion textureAtlasRegion, TextureAtlasRegion grayTextureAtlasRegion, UUID vaultID, Runnable onClick
      ) {
         this(position, Spatials.size(textureAtlasRegion), textureAtlasRegion, grayTextureAtlasRegion, vaultID, onClick);
      }

      public GrayedTextureAtlasElement(
         IPosition position, ISize size, TextureAtlasRegion textureAtlasRegion, TextureAtlasRegion grayTextureAtlasRegion, UUID vaultID, Runnable onClick
      ) {
         super(Spatials.positionXYZ(position).size(size));
         this.textureAtlasRegion = textureAtlasRegion;
         this.grayTextureAtlasRegion = grayTextureAtlasRegion;
         this.vaultID = vaultID;
         this.onClick = onClick;
         this.setVisible(true);
      }

      @Override
      public void setVisible(boolean visible) {
         this.visible = visible;
      }

      @Override
      public boolean isVisible() {
         return this.visible;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         boolean favorite = false;
         List<UUID> list = ClientHistoricFavoritesData.getFavorites();
         if (list != null) {
            for (UUID value : list) {
               if (value.equals(this.vaultID)) {
                  favorite = true;
                  break;
               }
            }
         }

         if (favorite) {
            renderer.render(this.textureAtlasRegion, poseStack, this.worldSpatial);
         } else {
            renderer.render(this.grayTextureAtlasRegion, poseStack, this.worldSpatial);
         }
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
         this.onClick.run();
         return true;
      }
   }

   public class HistoricContainerElement extends VerticalScrollClipContainer<VaultHistoricDataScreen.HistoricContainerElement> {
      List<VaultHistoricDataScreen.FavoritesDisplayElement> displayElements = new ArrayList<>();
      List<LabelElement> labelElements = new ArrayList<>();
      List<ButtonElement> buttonElements = new ArrayList<>();

      public void removeElements() {
         this.displayElements.forEach(x$0 -> this.removeElement(x$0));
         this.labelElements.forEach(x$0 -> this.removeElement(x$0));
         this.buttonElements.forEach(x$0 -> this.removeElement(x$0));
      }

      public void reset(boolean requiresFavorite) {
         this.reset(requiresFavorite, VaultHistoricDataScreen.this.snapshots);
      }

      public void reset(boolean requiresFavorite, List<VaultSnapshot> snap) {
         int i = 0;
         Iterator var4 = snap.iterator();

         while (true) {
            VaultExitContainerScreenData screenData;
            Vault vault;
            boolean favorite;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               VaultSnapshot snapshot = (VaultSnapshot)var4.next();
               screenData = new VaultExitContainerScreenData(snapshot, Minecraft.getInstance().player.getUUID());
               vault = screenData.getSnapshot().getEnd();
               if (!requiresFavorite) {
                  break;
               }

               UUID vaultId = vault.get(Vault.ID);
               List<UUID> list = ClientHistoricFavoritesData.getFavorites();
               favorite = false;
               if (list != null) {
                  for (UUID value : list) {
                     if (value.equals(vaultId)) {
                        favorite = true;
                        break;
                     }
                  }
               }
            } while (!favorite);

            VaultHistoricDataScreen.FavoritesDisplayElement<?> element = this.addElement(
               VaultHistoricDataScreen.this.new FavoritesDisplayElement(
                  Spatials.positionY(5 + 74 * i).positionX(62),
                  ScreenTextures.TAB_ICON_FAVORITES,
                  ScreenTextures.TAB_ICON_FAVORITES_GRAY,
                  new TextComponent("Vault Level: " + vault.get(Vault.LEVEL).get() + " - ")
                     .append(new TranslatableComponent(screenData.getCompletionTranslationString())),
                  276,
                  71,
                  new HashMap<>(),
                  new ArrayList<>(screenData.getQuickOverview()),
                  screenData
               )
            );
            this.displayElements.add(element);
            Component xpGainedComponent = new TextComponent("+ " + screenData.getStatsCollector().getExperience(vault) + " xp")
               .withStyle(Style.EMPTY.withColor(VaultHistoricDataScreen.XP_COLOR));
            LabelElement labelElement = this.addElement(
               (LabelElement)((LabelElement)new LabelElement(
                        Spatials.positionY(13 + 74 * i).positionX(330 - TextBorder.DEFAULT_FONT.get().width(xpGainedComponent)),
                        xpGainedComponent,
                        LabelTextStyle.shadow(ChatFormatting.BLACK)
                     )
                     .layout((screen, gui, parent, world) -> world.translateZ(3).width(TextBorder.DEFAULT_FONT.get().width(xpGainedComponent)).height(9)))
                  .tooltip(
                     Tooltips.multi(
                        () -> {
                           StatCollector statCollector = screenData.getStatsCollector();
                           long window = Minecraft.getInstance().getWindow().getWindow();
                           boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
                           float xpMultiplier = statCollector.getExpMultiplier();
                           List<Component> xpReceipt = VaultExitContainerScreenData.getXpReceipt(vault, screenData.getStatsCollector(), shiftDown, xpMultiplier);
                           int maxWidth = 0;
                           int spaceWidth = TextBorder.DEFAULT_FONT.get().width(" ");

                           for (Component component : xpReceipt) {
                              maxWidth = Math.max(TextBorder.DEFAULT_FONT.get().width(component), maxWidth);
                           }

                           MutableComponent titleText = new TextComponent("Vault Xp").withStyle(Style.EMPTY.withColor(VaultHistoricDataScreen.XP_COLOR));
                           MutableComponent spacer = new TextComponent("");
                           String totalXpString = shiftDown
                              ? String.format("(x%.1f) %s xp", xpMultiplier, ModConfigs.VAULT_STATS.getExperienceWithoutMultiplier(vault, statCollector))
                              : String.format("%s xp", statCollector.getExperience(vault));

                           for (int j = 0; j < maxWidth / spaceWidth - 10 - TextBorder.DEFAULT_FONT.get().width(totalXpString) / spaceWidth; j++) {
                              spacer.append(" ");
                           }

                           xpReceipt.add(0, titleText.append(spacer).append(totalXpString));
                           return xpReceipt;
                        }
                     )
                  )
            );
            this.labelElements.add(labelElement);
            Component buttonComponent = new TextComponent("Open").withStyle(ChatFormatting.WHITE);
            LabelElement labelElement2 = this.addElement(
               new LabelElement(
                  Spatials.positionXYZ(30 - TextBorder.DEFAULT_FONT.get().width(buttonComponent) / 2, 41 + 74 * i, 7),
                  buttonComponent,
                  LabelTextStyle.border4(ChatFormatting.BLACK).center()
               )
            );
            this.labelElements.add(labelElement2);
            ButtonElement<?> buttonElement = this.addElement(
               new ButtonElement<ButtonElement<ButtonElement<?>>>(
                     Spatials.positionXYZ(5, 36 + 74 * i, 5),
                     ScreenTextures.BUTTON_CLOSE_TEXTURES,
                     () -> {
                        Minecraft.getInstance()
                           .setScreen(
                              new VaultEndScreen(screenData.getSnapshot(), new TextComponent("Vault Exit"), Minecraft.getInstance().player.getUUID(), true)
                           );
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                     }
                  )
                  .layout((screen, gui, parent, world) -> world.width(52).height(19))
                  .tooltip(
                     (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                        tooltipRenderer.renderTooltip(
                           poseStack, List.of(new TextComponent("Open Vault Stats")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT
                        );
                        return false;
                     }
                  )
            );
            this.buttonElements.add(buttonElement);
            ButtonElement<?> buttonElement2 = this.addElement(
               new ButtonElement<ButtonElement<ButtonElement<?>>>(Spatials.positionXYZ(21, 13 + 74 * i, 5), ScreenTextures.BUTTON_SHARE_TEXTURES, () -> {
                  ModNetwork.CHANNEL.sendToServer(new ServerboundSendSnapshotLinkMessage(screenData.getSnapshot().getEnd().get(Vault.ID)));
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               }).layout((screen, gui, parent, world) -> world.width(19).height(19)).tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                  tooltipRenderer.renderTooltip(poseStack, List.of(new TextComponent("Share Vault")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
                  return false;
               })
            );
            this.buttonElements.add(buttonElement2);
            i++;
         }
      }

      public HistoricContainerElement(ISpatial spatial, List<VaultSnapshot> snapshots, boolean requiresFavorite) {
         super(spatial, Padding.of(2, 0));
         this.reset(requiresFavorite, snapshots);
      }
   }

   public class VaultHistoricDataTabElement<E extends VaultHistoricDataScreen.VaultHistoricDataTabElement<E>> extends ElasticContainerElement<E> {
      private int selectedIndex;

      public VaultHistoricDataTabElement(IPosition position, Consumer<Integer> selectedIndexChangeAction, boolean isCoop) {
         super(Spatials.positionXYZ(position));
         ObservableSupplier<Integer> selectedIndexObserver = ObservableSupplier.of(() -> this.selectedIndex, Integer::equals);
         selectedIndexObserver.ifChanged(selectedIndexChangeAction);
         this.addElement(
            new VaultHistoricDataScreen.VaultHistoricDataTabElement.StatTabElement(
               Spatials.positionZ(position).size(31, 28),
               new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_HISTORY),
               () -> this.selectedIndex == 0,
               () -> {
                  this.selectedIndex = 0;
                  selectedIndexObserver.ifChanged(selectedIndexChangeAction);
               },
               false
            )
         );
         this.addElement(
            new VaultHistoricDataScreen.VaultHistoricDataTabElement.StatTabElement(
               Spatials.positionY(31).positionZ(position).size(31, 28),
               new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_FAVORITES),
               () -> this.selectedIndex == 1,
               () -> {
                  this.selectedIndex = 1;
                  selectedIndexObserver.ifChanged(selectedIndexChangeAction);
               },
               false
            )
         );
      }

      private static class StatTabElement extends ElasticContainerElement<VaultHistoricDataScreen.VaultHistoricDataTabElement.StatTabElement> {
         private final Runnable onClick;

         public StatTabElement(ISpatial spatial, IRenderedElement iconElement, Supplier<Boolean> selected, Runnable onClick, boolean disabled) {
            super(spatial);
            this.onClick = onClick;
            this.addElement(
               new RenderIndexedElement(
                  Spatials.zero(),
                  List.of(
                     new TextureAtlasElement(Spatials.positionX(3), ScreenTextures.TAB_BACKGROUND_RIGHT),
                     new TextureAtlasElement(ScreenTextures.TAB_BACKGROUND_RIGHT_SELECTED),
                     new TextureAtlasElement(Spatials.positionX(3), ScreenTextures.TAB_BACKGROUND_RIGHT_DISABLED)
                  ),
                  () -> disabled ? 2 : (selected.get() ? 1 : 0)
               )
            );
            this.addElement(iconElement);
         }

         @Override
         public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
            this.onClick.run();
            return true;
         }
      }
   }
}
