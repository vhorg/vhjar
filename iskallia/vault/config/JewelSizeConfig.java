package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.gear.VaultGearRarity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class JewelSizeConfig extends Config {
   @Expose
   private Map<VaultGearRarity, IntRangeEntry> SIZES = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "jewel_size";
   }

   public Optional<IntRangeEntry> getSize(VaultGearRarity rarity) {
      return Optional.ofNullable(this.SIZES.get(rarity));
   }

   @Override
   protected void reset() {
      this.SIZES.clear();
      this.SIZES.put(VaultGearRarity.COMMON, new IntRangeEntry(15, 15));
      this.SIZES.put(VaultGearRarity.RARE, new IntRangeEntry(20, 20));
      this.SIZES.put(VaultGearRarity.EPIC, new IntRangeEntry(25, 25));
      this.SIZES.put(VaultGearRarity.OMEGA, new IntRangeEntry(30, 30));
   }
}
