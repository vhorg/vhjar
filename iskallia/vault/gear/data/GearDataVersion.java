package iskallia.vault.gear.data;

import java.util.function.Supplier;

public enum GearDataVersion {
   V0_1,
   V0_2;

   public static GearDataVersion current() {
      return values()[values().length - 1];
   }

   public boolean isLaterThan(GearDataVersion version) {
      return this.ordinal() > version.ordinal();
   }

   public <T> T readVersioned(Supplier<T> readFn, GearDataVersion currentVersion, T _default) {
      return this.isLaterThan(currentVersion) ? _default : readFn.get();
   }
}
