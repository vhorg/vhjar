package iskallia.vault.client.gui.framework.render.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ITooltipRenderFunction {
   ITooltipRenderFunction NONE = (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> false;

   boolean onHoverTooltip(ITooltipRenderer var1, @NotNull PoseStack var2, int var3, int var4, TooltipFlag var5);
}
