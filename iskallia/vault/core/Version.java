package iskallia.vault.core;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Version implements IVersion<Version> {
   v1_0("1.0"),
   v1_1("1.1"),
   v1_2("1.2"),
   v1_3("1.3"),
   v1_4("1.4"),
   v1_5("1.5"),
   v1_6("1.6"),
   v1_7("1.7"),
   v1_8("1.8"),
   v1_9("1.9"),
   v1_10("1.10"),
   v1_11("1.11"),
   v1_12("1.12"),
   v1_13("1.13"),
   v1_14("1.14"),
   v1_15("1.15"),
   v1_16("1.16"),
   v1_17("1.17"),
   v1_18("1.18"),
   v1_19("1.19"),
   v1_20("1.20"),
   v1_21("1.21"),
   v1_22("1.22"),
   v1_23("1.23"),
   v1_24("1.24"),
   v1_25("1.25"),
   v1_26("1.26"),
   v1_27("1.27"),
   v1_28("1.28"),
   v1_29("1.29"),
   v1_30("1.30"),
   v1_31("1.31");

   private static final Map<String, Version> NAME_TO_VERSION = Arrays.stream(values()).collect(Collectors.toMap(Version::getName, Function.identity()));
   private final String name;

   private Version(String name) {
      this.name = name;
   }

   public Version getThis() {
      return this;
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
}
