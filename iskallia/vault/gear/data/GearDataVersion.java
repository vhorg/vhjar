package iskallia.vault.gear.data;

import java.util.function.Supplier;

public enum GearDataVersion {
   V0_1,
   V0_2,
   V0_3,
   V0_4,
   V0_5;

   public static GearDataVersion current() {
      return values()[values().length - 1];
   }

   public boolean isLaterThan(GearDataVersion version) {
      return this.ordinal() > version.ordinal();
   }

   public <T> T readVersioned(GearDataVersion currentVersion, Supplier<T> readFn, Supplier<T> legacyFn) {
      return this.isLaterThan(currentVersion) ? legacyFn.get() : readFn.get();
   }
}
