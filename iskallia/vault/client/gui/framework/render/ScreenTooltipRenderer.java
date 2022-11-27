package iskallia.vault.client.gui.framework.render;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class ScreenTooltipRenderer implements ITooltipRenderer {
   protected final Screen screen;

   public static ScreenTooltipRenderer create(Screen screen) {
      return new ScreenTooltipRenderer(screen);
   }

   public ScreenTooltipRenderer(Screen screen) {
      this.screen = screen;
   }

   @Override
   public void renderTooltip(PoseStack poseStack, ItemStack itemStack, int mouseX, int mouseY, TooltipDirection direction) {
      this.renderWithDirection(
         direction, mouseX, () -> this.screen.renderTooltip(poseStack, this.getTooltipFromItem(itemStack), itemStack.getTooltipImage(), mouseX, mouseY)
      );
   }

   @Override
   public void renderTooltip(PoseStack poseStack, Component component, int mouseX, int mouseY, TooltipDirection direction) {
      this.renderWithDirection(direction, mouseX, () -> this.screen.renderTooltip(poseStack, component, mouseX, mouseY));
   }

   @Override
   public void renderTooltip(PoseStack poseStack, List<Component> tooltips, int mouseX, int mouseY, ItemStack itemStack, TooltipDirection direction) {
      this.renderWithDirection(direction, mouseX, () -> this.screen.renderTooltip(poseStack, tooltips, Optional.empty(), mouseX, mouseY, itemStack));
   }

   @Override
   public void renderTooltip(
      PoseStack poseStack, List<Component> tooltips, TooltipComponent tooltipComponent, int mouseX, int mouseY, TooltipDirection direction
   ) {
      this.renderWithDirection(direction, mouseX, () -> this.screen.renderTooltip(poseStack, tooltips, Optional.of(tooltipComponent), mouseX, mouseY));
   }

   @Override
   public void renderComponentTooltip(PoseStack poseStack, List<Component> tooltips, int mouseX, int mouseY, TooltipDirection direction) {
      this.renderWithDirection(direction, mouseX, () -> this.screen.renderComponentTooltip(poseStack, tooltips, mouseX, mouseY));
   }

   @Override
   public void renderComponentTooltip(
      PoseStack poseStack, List<? extends FormattedText> tooltips, int mouseX, int mouseY, ItemStack itemStack, TooltipDirection direction
   ) {
      this.renderWithDirection(direction, mouseX, () -> this.screen.renderComponentTooltip(poseStack, tooltips, mouseX, mouseY, null, itemStack));
   }

   @Override
   public void renderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> tooltips, int mouseX, int mouseY, TooltipDirection direction) {
      this.renderWithDirection(direction, mouseX, () -> this.screen.renderTooltip(poseStack, tooltips, mouseX, mouseY));
   }

   @Override
   public List<Component> getTooltipFromItem(ItemStack itemStack) {
      return this.screen.getTooltipFromItem(itemStack);
   }

   @Override
   public Font getTooltipFont(ItemStack itemStack) {
      return ForgeHooksClient.getTooltipFont(null, itemStack, this.screen.getMinecraft().font);
   }

   @Override
   public int getTooltipHeight(List<Component> tooltip) {
      int titlePadding = tooltip.size() <= 1 ? 0 : 2;
      return titlePadding
         + tooltip.stream().map(cmp -> ClientTooltipComponent.create(cmp.getVisualOrderText())).mapToInt(ClientTooltipComponent::getHeight).sum();
   }

   private void renderWithDirection(TooltipDirection direction, int mouseX, Runnable runnable) {
      if (direction == TooltipDirection.LEFT) {
         int width = this.screen.width;
         this.screen.width = mouseX;
         runnable.run();
         this.screen.width = width;
      } else if (direction == TooltipDirection.RIGHT) {
         runnable.run();
      }
   }
}
