package iskallia.vault.integration.jei;

import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

public class RemoveJEIContainerHandler<T extends AbstractContainerScreen<?>> implements IGuiContainerHandler<T> {
   @Nonnull
   public List<Rect2i> getGuiExtraAreas(@Nonnull T containerScreen) {
      return List.of(new Rect2i(-10000, -10000, 20000, 20000));
   }
}
