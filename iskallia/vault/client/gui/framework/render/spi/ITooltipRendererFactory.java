package iskallia.vault.client.gui.framework.render.spi;

import net.minecraft.client.gui.screens.Screen;

public interface ITooltipRendererFactory<S extends Screen> {
   ITooltipRenderer create(S var1);
}
