package iskallia.vault.client.gui.framework.element.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import org.jetbrains.annotations.NotNull;

public interface IRenderedElement extends IElement {
   void setVisible(boolean var1);

   boolean isVisible();

   void render(IElementRenderer var1, @NotNull PoseStack var2, int var3, int var4, float var5);
}
