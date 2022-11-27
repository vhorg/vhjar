package iskallia.vault.client.gui.screen.summary.element;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.summary.VaultEndScreen;
import iskallia.vault.client.gui.screen.summary.VaultExitContainerScreenData;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VaultCoopPlayersElement<E extends VaultCoopPlayersElement<E>> extends ContainerElement<E> {
   public VaultCoopPlayersElement(
      IPosition position, TextureAtlasRegion icon, int width, int height, Component name, Vault vault, Map<UUID, StatCollector> supplier
   ) {
      super(Spatials.positionXYZ(position).size(width, height + supplier.size() * 20 + 6));
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
                  Spatials.positionXYZ(4, 20, 1).size(width - 8, height - 20 + supplier.size() * 20), ScreenTextures.VAULT_EXIT_ELEMENT_BG
               )
               .layout((screen, gui, parent, world) -> world.size(width - 8, height - 20 + supplier.size() * 20))
         );
      }

      this.addElement(new TextureAtlasElement(Spatials.positionXYZ(8, 4, 5), icon));
      this.addElement(
         new VaultCoopPlayersElement.ChestStringElement(
            Spatials.positionXYZ(32, 8, 4), Spatials.size(16, 7), (Supplier<Component>)(() -> name), LabelTextStyle.shadow().left()
         )
      );
      AtomicInteger iterator = new AtomicInteger();
      supplier.forEach(
         (uuid, statCollector) -> {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
               Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
               MutableComponent playerName = (MutableComponent)(player == null
                  ? new TextComponent("Unknown")
                  : new TextComponent("").append(player.getDisplayName()));
               Component xpComponent = new TranslatableComponent(String.format("%s xp", statCollector.getExperience(vault)))
                  .withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR));
               int textWidth = TextBorder.DEFAULT_FONT.get().width(playerName);
               int xpWidth = TextBorder.DEFAULT_FONT.get().width(xpComponent);
               this.addElement(
                  (VaultCoopPlayersElement.ValueElement)new VaultCoopPlayersElement.ValueElement(
                        Spatials.positionXYZ(29, 30 + iterator.get() * 20, 2), Spatials.size(textWidth, 9), playerName, LabelTextStyle.shadow().left()
                     )
                     .tooltip(() -> playerName.append(" ").append(xpComponent))
               );
               this.addElement(
                  (VaultCoopPlayersElement.ValueElement)new VaultCoopPlayersElement.ValueElement(
                        Spatials.positionXYZ(width - 10 - xpWidth, 30 + iterator.get() * 20, 2),
                        Spatials.size(xpWidth, 9),
                        xpComponent,
                        LabelTextStyle.shadow()
                     )
                     .tooltip(
                        Tooltips.multi(
                           () -> {
                              long window = Minecraft.getInstance().getWindow().getWindow();
                              boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
                              float xpMultiplier = statCollector.getExpMultiplier();
                              List<Component> xpReceipt = VaultExitContainerScreenData.getXpReceipt(vault, statCollector, shiftDown, xpMultiplier);
                              int maxWidth = 0;
                              int spaceWidth = TextBorder.DEFAULT_FONT.get().width(" ");

                              for (Component component : xpReceipt) {
                                 maxWidth = Math.max(TextBorder.DEFAULT_FONT.get().width(component), maxWidth);
                              }

                              MutableComponent titleText = new TextComponent("Vault Xp").withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR));
                              MutableComponent spacer = new TextComponent("");
                              String totalXpString = shiftDown
                                 ? String.format("(x%.1f) %s xp", xpMultiplier, ModConfigs.VAULT_STATS.getExperienceWithoutMultiplier(vault, statCollector))
                                 : String.format("%s xp", statCollector.getExperience(vault));

                              for (int i = 0; i < maxWidth / spaceWidth - 10 - TextBorder.DEFAULT_FONT.get().width(totalXpString) / spaceWidth; i++) {
                                 spacer.append(" ");
                              }

                              xpReceipt.add(0, titleText.append(spacer).append(totalXpString));
                              return xpReceipt;
                           }
                        )
                     )
               );
               ClientPacketListener netHandler = Minecraft.getInstance().getConnection();
               if (netHandler != null) {
                  PlayerInfo info = netHandler.getPlayerInfo(uuid);
                  boolean offline = info == null;
                  ResourceLocation skin = offline ? DefaultPlayerSkin.getDefaultSkin() : info.getSkinLocation();
                  this.addElement(new HeadTextureElement(Spatials.positionXYZ(9, 26 + iterator.get() * 20, 2).size(16, 16), skin));
               }

               iterator.getAndIncrement();
            }
         }
      );
   }

   private static final class ChestStringElement extends DynamicLabelElement<Component, VaultCoopPlayersElement.ChestStringElement> {
      private ChestStringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class ValueElement extends DynamicLabelElement<Component, VaultCoopPlayersElement.ValueElement> {
      private ValueElement(IPosition position, ISize size, Component string, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, () -> string, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }
}
