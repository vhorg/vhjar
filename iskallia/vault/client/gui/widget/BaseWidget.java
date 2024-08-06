package iskallia.vault.client.gui.widget;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class BaseWidget implements Widget, GuiEventListener {
   protected int x;
   protected int y;
   protected int width;
   protected int height;
}
