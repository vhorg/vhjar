package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.stream.Stream;
import net.minecraft.network.chat.TextColor;

public class AbilitiesVignetteConfig extends Config {
   private static final String NAME = "abilities_vignette";
   @Expose
   public boolean ENABLED;
   @Expose
   public AbilitiesVignetteConfig.VignetteData GHOST_WALK;
   @Expose
   public AbilitiesVignetteConfig.VignetteData EMPOWER;
   @Expose
   public AbilitiesVignetteConfig.VignetteData EMPOWER_ICE_ARMOUR;
   @Expose
   public AbilitiesVignetteConfig.VignetteData EXECUTE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData MANA_SHIELD;
   @Expose
   public AbilitiesVignetteConfig.VignetteData MANA_SHIELD_RETRIBUTION;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAMPAGE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAMPAGE_LEECH;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAMPAGE_CHAIN;
   @Expose
   public AbilitiesVignetteConfig.VignetteData STONEFALL;
   @Expose
   public AbilitiesVignetteConfig.VignetteData STONEFALL_SHOCKWAVE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData STONEFALL_COLD;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAGE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData SHELL;
   @Expose
   public AbilitiesVignetteConfig.VignetteData SHELL_PORCUPINE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData SHELL_QUILL;

   @Override
   public String getName() {
      return "abilities_vignette";
   }

   @Override
   protected void reset() {
      this.ENABLED = true;
      this.GHOST_WALK = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#ABEABE"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.EMPOWER = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.EMPOWER_ICE_ARMOUR = new AbilitiesVignetteConfig.VignetteData(
         TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE
      );
      this.EXECUTE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.MANA_SHIELD = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#00FFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.MANA_SHIELD_RETRIBUTION = new AbilitiesVignetteConfig.VignetteData(
         TextColor.parseColor("#00FFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE
      );
      this.RAMPAGE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAMPAGE_LEECH = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAMPAGE_CHAIN = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.STONEFALL = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#999999"), 0.75F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.STONEFALL_SHOCKWAVE = new AbilitiesVignetteConfig.VignetteData(
         TextColor.parseColor("#FF6666"), 0.75F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE
      );
      this.STONEFALL_COLD = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#00CBFF"), 0.75F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.SHELL = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.SHELL_PORCUPINE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.SHELL_QUILL = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAGE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.5F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
   }

   @Override
   protected boolean isValid() {
      return this.getAll().noneMatch(vignetteData -> vignetteData.color == null);
   }

   protected Stream<AbilitiesVignetteConfig.VignetteData> getAll() {
      return Stream.of(
         this.GHOST_WALK,
         this.EMPOWER,
         this.EMPOWER_ICE_ARMOUR,
         this.SHELL_PORCUPINE,
         this.EXECUTE,
         this.MANA_SHIELD,
         this.MANA_SHIELD_RETRIBUTION,
         this.RAMPAGE,
         this.RAMPAGE_LEECH,
         this.RAMPAGE_CHAIN,
         this.STONEFALL,
         this.STONEFALL_SHOCKWAVE,
         this.STONEFALL_COLD,
         this.RAGE
      );
   }

   public static class VignetteData {
      @Expose
      public final TextColor color;
      @Expose
      public final float alpha;
      @Expose
      public final AbilitiesVignetteConfig.VignetteStyle style;

      public VignetteData(TextColor color, float alpha, AbilitiesVignetteConfig.VignetteStyle style) {
         this.color = color;
         this.alpha = alpha;
         this.style = style;
      }
   }

   public static enum VignetteStyle {
      @SerializedName("fill")
      FILL,
      @SerializedName("vignette")
      VIGNETTE;
   }
}
