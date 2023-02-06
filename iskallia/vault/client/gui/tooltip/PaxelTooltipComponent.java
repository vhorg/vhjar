package iskallia.vault.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.ToolViseScreen;
import iskallia.vault.config.PaxelConfigs;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.tool.PaxelItem;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class PaxelTooltipComponent implements ClientTooltipComponent {
   private static final ResourceLocation TEXTURE = ToolViseScreen.TEXTURE;
   private final PaxelItem.PaxelTooltip tooltip;
   private final Map<PaxelItem.Perk, Component> advanced = new HashMap<>();

   public PaxelTooltipComponent(PaxelItem.PaxelTooltip tooltip) {
      this.tooltip = tooltip;
      this.tooltip
         .perks
         .forEach(
            perk -> this.advanced
               .put(perk, new TextComponent(ModConfigs.PAXEL_CONFIGS.getPerkUpgrade(perk).getAdvancedTooltip()).withStyle(ChatFormatting.DARK_GRAY))
         );
   }

   public int getHeight() {
      return 20 + 18 * this.tooltip.perks.size();
   }

   public int getWidth(Font font) {
      int width = 56;
      float[] statValues = this.tooltip.statValues;

      for (int i = 0; i < statValues.length; i++) {
         float statValue = statValues[i];
         PaxelItem.Stat stat = this.tooltip.stats[i];
         PaxelConfigs.Upgrade upgradeCfg = ModConfigs.PAXEL_CONFIGS.getUpgrade(stat);
         String valueStr = upgradeCfg.formatValue(statValue);
         width += font.width(valueStr) + 2;
      }

      int perkMax = 16;
      int max = 0;

      for (Entry<PaxelItem.Perk, Component> entry : this.advanced.entrySet()) {
         MutableComponent cmp = new TextComponent(entry.getKey().getSerializedName());
         if (InputEvents.isShiftDown()) {
            cmp.append(" ").append(entry.getValue());
         }

         max = Math.max(max, font.width(cmp));
      }

      return Math.max(width, perkMax + max);
   }

   public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer ir, int zLevel) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, zLevel);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int uStat = 0;
      int vStat = 218;
      int xOffsetStat = x;
      int widthHeight = 14;
      float[] statValues = this.tooltip.statValues;

      for (int i = 0; i < statValues.length; i++) {
         float statValue = statValues[i];
         PaxelItem.Stat stat = this.tooltip.stats[i];
         PaxelConfigs.Upgrade upgradeCfg = ModConfigs.PAXEL_CONFIGS.getUpgrade(stat);
         String valueStr = upgradeCfg.formatValue(statValue);
         RenderSystem.setShaderTexture(0, TEXTURE);
         GuiComponent.blit(poseStack, xOffsetStat, y, widthHeight, widthHeight, uStat, vStat, widthHeight, widthHeight, 256, 256);
         uStat += 14;
         Component text = new TextComponent(valueStr);
         xOffsetStat += widthHeight;
         font.draw(poseStack, text, xOffsetStat, y + 4, -1);
         xOffsetStat += font.width(text) + 2;
      }

      int uPerk = 0;
      int vPerk = 232;
      boolean shift = InputEvents.isShiftDown();

      for (PaxelItem.Perk perk : this.tooltip.perks) {
         y += 16;
         RenderSystem.setShaderTexture(0, TEXTURE);
         GuiComponent.blit(poseStack, x, y, widthHeight, widthHeight, uPerk + widthHeight * perk.ordinal(), vPerk, widthHeight, widthHeight, 256, 256);
         MutableComponent perkText = new TextComponent(perk.getSerializedName())
            .withStyle(Style.EMPTY.withColor(ModConfigs.PAXEL_CONFIGS.getPerkUpgrade(perk).getColor()));
         if (shift) {
            perkText.append(" ").append(this.advanced.get(perk));
         }

         font.draw(poseStack, perkText, x + 2 + widthHeight, y + 4, -1);
      }

      poseStack.popPose();
   }
}
