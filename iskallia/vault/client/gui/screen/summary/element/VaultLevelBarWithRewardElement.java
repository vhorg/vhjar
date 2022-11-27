package iskallia.vault.client.gui.screen.summary.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.HorizontalProgressBarElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.summary.VaultExitContainerScreenData;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class VaultLevelBarWithRewardElement<E extends VaultLevelBarWithRewardElement<E>> extends ContainerElement<E> {
   public static final TextureAtlasRegion BLANK = ScreenTextures.BLANK;
   public static final TextureAtlasRegion BACKGROUND = ScreenTextures.VAULT_LEVEL_BAR_BACKGROUND;
   public static final TextureAtlasRegion FOREGROUND = ScreenTextures.VAULT_LEVEL_BAR;
   public static final TextureAtlasRegion FOREGROUND_GAINED_XP = ScreenTextures.VAULT_LEVEL_BAR_GAINED_XP;
   public static final TextColor TEXT_COLOR = TextColor.parseColor("#FFE637");
   public static final TextColor BORDER_COLOR = TextColor.parseColor("#3E3E3E");
   private VaultLevelBarWithRewardElement.StringElement stringElement;

   public VaultLevelBarWithRewardElement(IPosition position, int width, VaultExitContainerScreenData screenData) {
      super(Spatials.positionXYZ(position).size(width, 24));
      this.addElement(
         new HorizontalProgressBarElement(
            Spatials.positionXYZ(42, 10, 9),
            BACKGROUND,
            FOREGROUND_GAINED_XP,
            () -> screenData.getVaultLevelPercentageWithReward(screenData.getSnapshot().getEnd()),
            HorizontalProgressBarElement.Direction.LEFT_TO_RIGHT
         )
      );
      this.addElement(
         new HorizontalProgressBarElement(
            Spatials.positionXYZ(42, 10, 11), BLANK, FOREGROUND, screenData::getVaultLevelPercentage, HorizontalProgressBarElement.Direction.LEFT_TO_RIGHT
         )
      );
      this.addElement(
         new VaultLevelBarWithRewardElement.StringElement(
            Spatials.positionXYZ(8, 8, 200),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent(String.valueOf(screenData.getVaultLevel())).withStyle(Style.EMPTY.withColor(TEXT_COLOR))),
            LabelTextStyle.shadow(BORDER_COLOR).center()
         )
      );
      int expGained = screenData.getStatsCollector().getExperience(screenData.getSnapshot().getEnd());
      this.stringElement = this.addElement(
         new VaultLevelBarWithRewardElement.StringElement(
            Spatials.positionXYZ(
               width / 2 - TextBorder.DEFAULT_FONT.get().width(new TextComponent(VaultBarOverlay.vaultExp + expGained + " / " + VaultBarOverlay.tnl)) / 2,
               3,
               200
            ),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent(VaultBarOverlay.vaultExp + expGained + " / " + VaultBarOverlay.tnl)
               .withStyle(Style.EMPTY.withColor(TEXT_COLOR))),
            LabelTextStyle.shadow(BORDER_COLOR)
         )
      );
      this.stringElement.setVisible(false);
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 5).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 4).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.stringElement.setVisible(this.containsMouse(mouseX, mouseY));
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }

   private static final class StringElement extends DynamicLabelElement<Component, VaultLevelBarWithRewardElement.StringElement> {
      private StringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }
}
