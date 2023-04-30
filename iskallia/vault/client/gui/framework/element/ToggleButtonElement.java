package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class ToggleButtonElement extends NineSliceButtonElement<ToggleButtonElement> {
   protected Supplier<String> append;

   public ToggleButtonElement(ISpatial spatial, Component title, Supplier<String> append, Runnable onClick) {
      super(spatial, ScreenTextures.BUTTON_EMPTY_TEXTURES, onClick);
      this.label(() -> title, LabelTextStyle.shadow().center());
      this.append = append;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }

   @Override
   protected void renderLabel(IElementRenderer renderer, PoseStack poseStack, int x, int y, int z, int width) {
      MutableComponent label = this.component.get().copy().append(": ").append(this.append.get());
      this.labelTextStyle.textBorder().render(renderer, poseStack, label, this.labelTextStyle.textWrap(), this.labelTextStyle.textAlign(), x, y, z, width);
   }
}
