package iskallia.vault.gear.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class ModifierCategoryTooltip {
   public static MutableComponent modifyLegendaryTooltip(MutableComponent cmp) {
      Style style = cmp.getStyle();
      String rawString = cmp.getString();
      MutableComponent legendaryCt = new TextComponent("✦ ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(15853364)));
      int time = rawString.length();
      time = (int)(time * 1.4);
      int step = (int)(System.currentTimeMillis() / 90L % time);
      if (step >= rawString.length()) {
         return legendaryCt.append(new TextComponent(rawString).setStyle(style));
      } else {
         int stepCap = Math.min(step + 1, rawString.length());
         String start = rawString.substring(0, step);
         String highlight = rawString.substring(step, stepCap);
         String end = rawString.substring(stepCap);
         return legendaryCt.append(new TextComponent(start).setStyle(style))
            .append(new TextComponent(highlight).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
            .append(new TextComponent(end).setStyle(style));
      }
   }

   public static MutableComponent modifyAbyssalTooltip(MutableComponent cmp) {
      Style style = cmp.getStyle();
      String rawString = cmp.getString();
      MutableComponent abyssalCt = new TextComponent("ᚼ ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(29260)));
      return abyssalCt.append(new TextComponent(rawString).setStyle(style));
   }
}
