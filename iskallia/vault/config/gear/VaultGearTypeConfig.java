package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.util.data.WeightedList;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class VaultGearTypeConfig extends Config {
   @Expose
   private String defaultRoll;
   @Expose
   private Map<String, VaultGearTypeConfig.RollType> rolls = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "gear%sgear_roll_type".formatted(File.separator);
   }

   public VaultGearTypeConfig.RollType getDefaultRoll() {
      return this.getRollPool(this.defaultRoll)
         .orElseThrow(() -> new IllegalArgumentException("No RollType defined for roll type '%s'".formatted(this.defaultRoll)));
   }

   public Set<String> getRollPoolNames() {
      return this.rolls.keySet();
   }

   public Optional<VaultGearTypeConfig.RollType> getRollPool(String typeName) {
      return Optional.ofNullable(this.rolls.get(typeName)).map(type -> type.setName(typeName));
   }

   @Override
   protected void reset() {
      this.rolls.clear();
      this.defaultRoll = "Scrappy";
      this.rolls.put("Scrappy", new VaultGearTypeConfig.RollType(new WeightedList<VaultGearRarity>().add(VaultGearRarity.SCRAPPY, 1)));
      this.rolls
         .put(
            "Scrappy+",
            new VaultGearTypeConfig.RollType(
               new WeightedList<VaultGearRarity>()
                  .add(VaultGearRarity.SCRAPPY, 120)
                  .add(VaultGearRarity.COMMON, 20)
                  .add(VaultGearRarity.RARE, 10)
                  .add(VaultGearRarity.EPIC, 3)
                  .add(VaultGearRarity.OMEGA, 1)
            )
         );
   }

   public static class RollType {
      private String name;
      @Expose
      private WeightedList<VaultGearRarity> pool;
      @Expose
      private int color;

      public RollType(WeightedList<VaultGearRarity> pool) {
         this.pool = pool;
      }

      private VaultGearTypeConfig.RollType setName(String name) {
         this.name = name;
         return this;
      }

      public String getName() {
         return this.name;
      }

      public int getColor() {
         return this.color;
      }

      public VaultGearRarity getRandom(Random random) {
         return this.pool.getRandom(random);
      }
   }
}
