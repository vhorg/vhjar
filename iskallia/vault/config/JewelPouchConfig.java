package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.init.ModItems;
import java.util.Optional;

public class JewelPouchConfig extends Config {
   @Expose
   private SingleItemEntry jewel;
   @Expose
   private int identifiedJewelCount;
   @Expose
   private int unidentifiedJewelCount;

   @Override
   public String getName() {
      return "jewel_pouch";
   }

   public Optional<SingleItemEntry> getJewel() {
      return Optional.ofNullable(this.jewel);
   }

   public int getIdentifiedJewelCount() {
      return this.identifiedJewelCount;
   }

   public int getUnidentifiedJewelCount() {
      return this.unidentifiedJewelCount;
   }

   @Override
   protected void reset() {
      this.jewel = new SingleItemEntry(ModItems.JEWEL);
      this.identifiedJewelCount = 2;
      this.unidentifiedJewelCount = 1;
   }
}
