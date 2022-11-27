package iskallia.vault.client.gui.framework.render;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public final class Tooltips {
   public static final Component DEFAULT_HOLD_SHIFT_COMPONENT = new TextComponent("")
      .append(new TextComponent("Hold ").withStyle(ChatFormatting.DARK_GRAY))
      .append(new TextComponent("<SHIFT>").withStyle(ChatFormatting.GRAY))
      .append(new TextComponent(" for more info.").withStyle(ChatFormatting.DARK_GRAY));

   public static ITooltipRenderFunction single(Supplier<Component> componentSupplier) {
      return single(TooltipDirection.RIGHT, componentSupplier);
   }

   public static ITooltipRenderFunction single(TooltipDirection direction, Supplier<Component> componentSupplier) {
      return (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         tooltipRenderer.renderTooltip(poseStack, componentSupplier.get(), mouseX, mouseY, direction);
         return true;
      };
   }

   public static ITooltipRenderFunction multi(Supplier<List<Component>> componentSupplier) {
      return multi(TooltipDirection.RIGHT, componentSupplier);
   }

   public static ITooltipRenderFunction multi(TooltipDirection direction, Supplier<List<Component>> componentSupplier) {
      return (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         tooltipRenderer.renderComponentTooltip(poseStack, componentSupplier.get(), mouseX, mouseY, direction);
         return true;
      };
   }

   public static ITooltipRenderFunction advanced(ITooltipRenderFunction tooltipRenderFunction, ITooltipRenderFunction advancedTooltipRenderFunction) {
      return (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> tooltipFlag.isAdvanced()
         ? tooltipRenderFunction.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag)
         : advancedTooltipRenderFunction.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag);
   }

   public static ITooltipRenderFunction shift(ITooltipRenderFunction tooltipRenderFunction, ITooltipRenderFunction shiftTooltipRenderFunction) {
      return (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         long window = Minecraft.getInstance().getWindow().getWindow();
         return !InputConstants.isKeyDown(window, 340) && !InputConstants.isKeyDown(window, 344)
            ? tooltipRenderFunction.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag)
            : shiftTooltipRenderFunction.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag);
      };
   }

   private Tooltips() {
   }
}
