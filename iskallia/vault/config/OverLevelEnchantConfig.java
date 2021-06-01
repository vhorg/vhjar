package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.EnchantedBookEntry;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

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

   public IFormattableTextComponent getPrefixFor(int overlevel) {
      EnchantedBookEntry tier = this.getTier(overlevel);
      if (tier == null) {
         return null;
      } else {
         StringTextComponent prefix = new StringTextComponent(tier.getPrefix() + " ");
         prefix.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240745_a_(tier.getColorHex())));
         return prefix;
      }
   }

   public IFormattableTextComponent format(ITextComponent baseName, int overlevel) {
      EnchantedBookEntry tier = this.getTier(overlevel);
      if (tier == null) {
         return null;
      } else {
         IFormattableTextComponent prefix = new StringTextComponent(tier.getPrefix() + " ").func_230529_a_(baseName);
         prefix.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240745_a_(tier.getColorHex())));
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
