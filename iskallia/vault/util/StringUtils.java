package iskallia.vault.util;

import java.util.Set;

public class StringUtils {
   private static final Set<String> LOWERCASE_WORDS = Set.of("of", "a", "an", "and", "the", "but", "or", "nor", "for", "so", "yet", "is");

   public static String truncateMaxLength(String original, int maxLength) {
      if (maxLength < 0) {
         throw new IllegalArgumentException();
      } else {
         return original != null && original.length() > maxLength ? original.substring(0, maxLength) : original;
      }
   }

   public static String convertToTitleCase(String input) {
      if (input != null && !input.isEmpty()) {
         StringBuilder titleCase = new StringBuilder();
         StringBuilder currentWord = new StringBuilder();

         for (char c : input.toCharArray()) {
            if (c == '_') {
               processWord(currentWord, titleCase);
               titleCase.append(' ');
            } else {
               currentWord.append(c);
            }
         }

         processWord(currentWord, titleCase);
         return titleCase.toString();
      } else {
         return input;
      }
   }

   private static void processWord(StringBuilder currentWord, StringBuilder titleCase) {
      if (!currentWord.isEmpty()) {
         String word = currentWord.toString();
         if (LOWERCASE_WORDS.contains(word.toLowerCase())) {
            titleCase.append(word.toLowerCase());
         } else {
            titleCase.append(Character.toTitleCase(word.charAt(0)));
            titleCase.append(word.substring(1).toLowerCase());
         }

         currentWord.setLength(0);
      }
   }
}
