package iskallia.vault.client.gui.framework.element.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TooltipFlag.Default;
import org.jetbrains.annotations.NotNull;

public interface ITooltipElement extends ISpatialElement {
   default boolean renderTooltip(ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY) {
      return this.isEnabled()
         && this.contains(mouseX, mouseY)
         && this.onHoverTooltip(
            tooltipRenderer, poseStack, mouseX, mouseY, Minecraft.getInstance().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL
         );
   }

   boolean onHoverTooltip(ITooltipRenderer var1, @NotNull PoseStack var2, int var3, int var4, TooltipFlag var5);
}
