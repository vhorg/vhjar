package iskallia.vault.gear.tooltip;

import iskallia.vault.util.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class ModifierCategoryTooltip {
   public static MutableComponent modifyLegendaryTooltip(MutableComponent cmp) {
      MutableComponent legendaryCt = new TextComponent("✦ ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(15853364)));
      int cmpLength = TextComponentUtils.getLength(cmp);
      int time = (int)(cmpLength * 1.4);
      int step = (int)(System.currentTimeMillis() / 90L % time);
      if (step >= cmpLength) {
         return new TextComponent("").append(legendaryCt).append(cmp);
      } else {
         int stepCap = Math.min(step + 1, cmpLength);
         CommandSourceStack stack = TextComponentUtils.createClientSourceStack();
         MutableComponent startCmp = TextComponentUtils.substring(stack, cmp, 0, step);
         MutableComponent highlight = TextComponentUtils.substring(stack, cmp, step, stepCap);
         MutableComponent endCmp = TextComponentUtils.substring(stack, cmp, stepCap);
         TextComponentUtils.applyStyle(highlight, Style.EMPTY.withColor(ChatFormatting.WHITE));
         return new TextComponent("").append(legendaryCt).append(startCmp).append(highlight).append(endCmp);
      }
   }

   public static MutableComponent modifyAbyssalTooltip(MutableComponent cmp) {
      MutableComponent abyssalCt = new TextComponent("ᚼ ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(29260)));
      return abyssalCt.append(cmp);
   }

   public static MutableComponent modifyCraftedTooltip(MutableComponent cmp) {
      MutableComponent abyssalCt = new TextComponent("⛏ ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16109454)));
      return abyssalCt.append(cmp);
   }
}
