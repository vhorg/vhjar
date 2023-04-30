package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SkillStyle;
import java.util.HashMap;

public class ExpertisesGUIConfig extends Config {
   @Expose
   private HashMap<String, SkillStyle> styles;

   @Override
   public String getName() {
      return "expertises_gui_styles";
   }

   public HashMap<String, SkillStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
   }
}
