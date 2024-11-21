package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class JewelCuttingButtonElement<E extends JewelCuttingButtonElement<E>> extends ButtonElement<E> {
   private static final Random rand = new Random();

   public JewelCuttingButtonElement(IPosition position, Runnable onClick, VaultJewelCuttingStationContainer container) {
      super(position, ScreenTextures.BUTTON_CRAFT_TEXTURES, onClick);
   }

   private Component addTooltipDots(int amount, ChatFormatting formatting) {
      return new TextComponent("⬢ ".repeat(Math.max(0, amount))).withStyle(formatting);
   }
}
