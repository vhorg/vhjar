package iskallia.vault.util;

public class StringUtils {
   public static String truncateMaxLength(String original, int maxLength) {
      if (maxLength < 0) {
         throw new IllegalArgumentException();
      } else {
         return original != null && original.length() > maxLength ? original.substring(0, maxLength) : original;
      }
   }
}
