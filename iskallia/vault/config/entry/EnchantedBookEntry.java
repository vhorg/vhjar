package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;

public class EnchantedBookEntry {
   @Expose
   private int extraLevel;
   @Expose
   private int levelNeeded;
   @Expose
   private String prefix;
   @Expose
   private String colorHex;

   public EnchantedBookEntry(int extraLevel, int levelNeeded, String prefix, String colorHex) {
      this.extraLevel = extraLevel;
      this.levelNeeded = levelNeeded;
      this.prefix = prefix;
      this.colorHex = colorHex;
   }

   public int getExtraLevel() {
      return this.extraLevel;
   }

   public int getLevelNeeded() {
      return this.levelNeeded;
   }

   public String getColorHex() {
      return this.colorHex;
   }

   public String getPrefix() {
      return this.prefix;
   }
}
