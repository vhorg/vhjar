package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
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

   public Map<String, ResearchGroupStyle> getStyles() {
      return this.groupStyles;
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
            "Storage",
            ResearchGroupStyle.builder("Storage")
               .withHeaderColor(Color.ORANGE.getRGB())
               .withHeaderTextColor(Color.LIGHT_GRAY.getRGB())
               .withPosition(-10, -25)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/storage"))
               .build()
         );
      this.groupStyles
         .put(
            "VaultUtils",
            ResearchGroupStyle.builder("VaultUtils")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(180, 295)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/vault_utils"))
               .build()
         );
      this.groupStyles
         .put(
            "Power",
            ResearchGroupStyle.builder("Power")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(370, -25)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/power"))
               .build()
         );
      this.groupStyles
         .put(
            "Farming",
            ResearchGroupStyle.builder("Farming")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(-10, 135)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/farming"))
               .build()
         );
      this.groupStyles
         .put(
            "Processing",
            ResearchGroupStyle.builder("Processing")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(180, 135)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/processing"))
               .build()
         );
      this.groupStyles
         .put(
            "Base",
            ResearchGroupStyle.builder("Base")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(370, 135)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/base"))
               .build()
         );
      this.groupStyles
         .put(
            "Decoration",
            ResearchGroupStyle.builder("Decoration")
               .withHeaderColor(Color.BLUE.getRGB())
               .withHeaderTextColor(Color.WHITE.getRGB())
               .withPosition(180, -25)
               .withBoxSize(170, 145)
               .withIcon(VaultMod.id("gui/research_groups/decoration"))
               .build()
         );
   }
}
