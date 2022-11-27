package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.EnchantedBookEntry;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class OverLevelEnchantConfig extends Config {
   @Expose
   private List<EnchantedBookEntry> BOOK_TIERS;

   public List<EnchantedBookEntry> getBookTiers() {
      return this.BOOK_TIERS;
   }

   public EnchantedBookEntry getTier(int overlevel) {
      for (EnchantedBookEntry tier : this.BOOK_TIERS) {
         if (tier.getExtraLevel() == overlevel) {
            return tier;
         }
      }

      return null;
   }

   public MutableComponent getPrefixFor(int overlevel) {
      EnchantedBookEntry tier = this.getTier(overlevel);
      if (tier == null) {
         return null;
      } else {
         TextComponent prefix = new TextComponent(tier.getPrefix() + " ");
         prefix.setStyle(Style.EMPTY.withColor(TextColor.parseColor(tier.getColorHex())));
         return prefix;
      }
   }

   public MutableComponent format(Component baseName, int overlevel) {
      EnchantedBookEntry tier = this.getTier(overlevel);
      if (tier == null) {
         return null;
      } else {
         MutableComponent prefix = new TextComponent(tier.getPrefix() + " ").append(baseName);
         prefix.setStyle(Style.EMPTY.withColor(TextColor.parseColor(tier.getColorHex())));
         return prefix;
      }
   }

   @Override
   public String getName() {
      return "overlevel_enchant";
   }

   @Override
   protected void reset() {
      this.BOOK_TIERS = new LinkedList<>();
      this.BOOK_TIERS.add(new EnchantedBookEntry(1, 40, "Ancient", "#ffae00"));
      this.BOOK_TIERS.add(new EnchantedBookEntry(2, 60, "Super", "#ff6c00"));
      this.BOOK_TIERS.add(new EnchantedBookEntry(3, 80, "Legendary", "#ff3600"));
   }
}
