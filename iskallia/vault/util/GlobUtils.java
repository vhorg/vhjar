package iskallia.vault.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobUtils {
   public static boolean matches(String glob, String text) {
      Pattern pattern = Pattern.compile("(?s)^\\Q" + glob.replace("\\E", "\\E\\\\E\\Q").replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q") + "\\E$");
      Matcher matcher = pattern.matcher(text);
      return matcher.matches();
   }
}
