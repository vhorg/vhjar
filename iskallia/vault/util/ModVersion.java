package iskallia.vault.util;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;

public class ModVersion implements ArtifactVersion {
   private final String value;
   private Integer majorVersion;
   private Integer minorVersion;
   private Integer incrementalVersion;
   private Integer buildNumber;
   private String qualifier;

   public ModVersion(String version) {
      this.value = version;
      this.parseVersion(version);
   }

   public int getMajorVersion() {
      return this.majorVersion != null ? this.majorVersion : 0;
   }

   public int getMinorVersion() {
      return this.minorVersion != null ? this.minorVersion : 0;
   }

   public int getIncrementalVersion() {
      return this.incrementalVersion != null ? this.incrementalVersion : 0;
   }

   public int getBuildNumber() {
      return this.buildNumber != null ? this.buildNumber : 0;
   }

   public String getQualifier() {
      return this.qualifier == null ? "" : this.qualifier;
   }

   public final void parseVersion(String version) {
      try {
         int index = version.indexOf(45);
         String part2 = null;
         String part1;
         if (index < 0) {
            part1 = version;
         } else {
            part1 = version.substring(0, index);
            part2 = version.substring(index + 1);
         }

         if (part2 != null) {
            this.qualifier = part2;
         }

         String[] numbers = part1.split("\\.");
         if (numbers.length == 1) {
            this.majorVersion = Integer.parseInt(numbers[0]);
         } else if (numbers.length == 2) {
            this.majorVersion = Integer.parseInt(numbers[0]);
            this.minorVersion = Integer.parseInt(numbers[1]);
         } else if (numbers.length == 3) {
            this.majorVersion = Integer.parseInt(numbers[0]);
            this.minorVersion = Integer.parseInt(numbers[1]);
            this.incrementalVersion = Integer.parseInt(numbers[2]);
         } else if (numbers.length == 4) {
            this.majorVersion = Integer.parseInt(numbers[0]);
            this.minorVersion = Integer.parseInt(numbers[1]);
            this.incrementalVersion = Integer.parseInt(numbers[2]);
            this.buildNumber = Integer.parseInt(numbers[3]);
         }
      } catch (NumberFormatException var6) {
      }
   }

   @Override
   public String toString() {
      return this.value;
   }

   public int compareTo(@NotNull ArtifactVersion o) {
      return 0;
   }

   public boolean accepted(String version) {
      ModVersion other = new ModVersion(version);
      if (this.getMajorVersion() != other.getMajorVersion()) {
         return false;
      } else {
         return this.getMinorVersion() != other.getMinorVersion() ? false : this.getIncrementalVersion() == other.getIncrementalVersion();
      }
   }
}
