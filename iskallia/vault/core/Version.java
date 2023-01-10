package iskallia.vault.core;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Version {
   v1_0("1.0"),
   v1_1("1.1"),
   v1_2("1.2"),
   v1_3("1.3"),
   v1_4("1.4"),
   v1_5("1.5");

   private static final Map<String, Version> NAME_TO_VERSION = Arrays.stream(values()).collect(Collectors.toMap(Version::getName, Function.identity()));
   private final String name;

   private Version(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static Version fromName(String name) {
      return NAME_TO_VERSION.get(name);
   }

   public static Version latest() {
      return values()[values().length - 1];
   }

   public static Version oldest() {
      return values()[0];
   }

   public boolean isNewerThan(Version v) {
      return this.compareTo(v) > 0;
   }

   public boolean isNewerOrEqualTo(Version v) {
      return this.compareTo(v) >= 0;
   }

   public boolean isOlderThan(Version v) {
      return this.compareTo(v) < 0;
   }

   public boolean isOlderOrEqualTo(Version v) {
      return this.compareTo(v) <= 0;
   }

   public boolean isEqualTo(Version v) {
      return this.compareTo(v) == 0;
   }

   public static Predicate<Version> newerThan(Version version) {
      return v -> v.isNewerThan(version);
   }

   public static Predicate<Version> newerOrEqualTo(Version version) {
      return v -> v.isNewerOrEqualTo(version);
   }

   public static Predicate<Version> olderThan(Version version) {
      return v -> v.isOlderThan(version);
   }

   public static Predicate<Version> olderOrEqualTo(Version version) {
      return v -> v.isOlderOrEqualTo(version);
   }

   public static Predicate<Version> equalTo(Version version) {
      return v -> v.isEqualTo(version);
   }
}
