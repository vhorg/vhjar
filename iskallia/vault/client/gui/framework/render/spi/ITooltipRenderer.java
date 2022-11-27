package iskallia.vault.client.gui.framework.render.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public interface ITooltipRenderer {
   void renderTooltip(PoseStack var1, ItemStack var2, int var3, int var4, TooltipDirection var5);

   void renderTooltip(PoseStack var1, Component var2, int var3, int var4, TooltipDirection var5);

   void renderTooltip(PoseStack var1, List<Component> var2, int var3, int var4, ItemStack var5, TooltipDirection var6);

   void renderTooltip(PoseStack var1, List<Component> var2, TooltipComponent var3, int var4, int var5, TooltipDirection var6);

   void renderComponentTooltip(PoseStack var1, List<Component> var2, int var3, int var4, TooltipDirection var5);

   void renderComponentTooltip(PoseStack var1, List<? extends FormattedText> var2, int var3, int var4, ItemStack var5, TooltipDirection var6);

   void renderTooltip(PoseStack var1, List<? extends FormattedCharSequence> var2, int var3, int var4, TooltipDirection var5);

   List<Component> getTooltipFromItem(ItemStack var1);

   Font getTooltipFont(ItemStack var1);

   int getTooltipHeight(List<Component> var1);
}
