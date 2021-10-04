package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ResearchGroupStyle;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ResearchGroupStyleConfig extends Config {
   @Expose
   protected Map<String, ResearchGroupStyle> groupStyles = new HashMap<>();

   @Nullable
   public ResearchGroupStyle getStyle(String groupId) {
      return this.groupStyles.get(groupId);
   }

   @Override
   public String getName() {
      return "researches_groups_styles";
   }

   @Override
   protected void reset() {
      this.groupStyles.clear();
      this.groupStyles
         .put(
            "StorageGroup",
            ResearchGroupStyle.builder("StorageGroup")
               .withHeaderColor(Color.ORANGE.getRGB())
               .withHeaderTextColor(Color.LIGHT_GRAY.getRGB())
               .withPosition(-25, -35)
               .withBoxSize(125, 110)
               .withIcon(208, 0)
               .build()
         );
      this.groupStyles
         .put(
            "MagicGroup",
            ResearchGroupStyle.builder("MagicGroup")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(-25, 115)
               .withBoxSize(135, 150)
               .withIcon(176, 16)
               .build()
         );
   }
}
