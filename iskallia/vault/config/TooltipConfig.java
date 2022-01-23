package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.Item;

public class TooltipConfig extends Config {
   @Expose
   private final List<TooltipConfig.TooltipEntry> tooltips = new ArrayList<>();

   public Optional<String> getTooltipString(Item item) {
      String itemRegistryName = item.getRegistryName().toString();
      return this.tooltips.stream().filter(entry -> entry.item.equals(itemRegistryName)).map(TooltipConfig.TooltipEntry::getValue).findFirst();
   }

   @Override
   public String getName() {
      return "tooltip";
   }

   @Override
   protected void reset() {
      this.tooltips.clear();
      this.tooltips
         .add(
            new TooltipConfig.TooltipEntry(ModItems.POISONOUS_MUSHROOM.getRegistryName().toString(), "Rare - Crafting ingredient for Mystery Stews and Burgers")
         );
   }

   public static class TooltipEntry {
      @Expose
      private String item;
      @Expose
      private String value;

      public TooltipEntry(String item, String value) {
         this.item = item;
         this.value = value;
      }

      public String getItem() {
         return this.item;
      }

      public String getValue() {
         return this.value;
      }
   }
}
