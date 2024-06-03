package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.screen.block.CrystalWorkbenchScreen;
import iskallia.vault.container.CrystalWorkbenchContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public class CrystalWorkbenchButtonElement<E extends CrystalWorkbenchButtonElement<E>> extends ButtonElement<E> {
   public CrystalWorkbenchButtonElement(IPosition position, Runnable onClick, CrystalWorkbenchScreen screen) {
      super(position, ScreenTextures.BUTTON_CRAFT_TEXTURES, onClick);
      this.tooltip(Tooltips.multi(() -> {
         ItemStack stack = ((CrystalWorkbenchContainer)screen.getMenu()).getSlot(36).getItem();
         List<Component> tooltip = new ArrayList<>();
         if (stack.isEmpty()) {
            tooltip.add(new TextComponent("• Missing Input").withStyle(ChatFormatting.RED));
         }

         return tooltip;
      }));
   }
}
