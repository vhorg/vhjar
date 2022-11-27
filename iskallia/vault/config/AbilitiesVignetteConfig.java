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
   public AbilitiesVignetteConfig.VignetteData TANK;
   @Expose
   public AbilitiesVignetteConfig.VignetteData TANK_PROJECTILE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData TANK_REFLECT;
   @Expose
   public AbilitiesVignetteConfig.VignetteData EXECUTE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData MANA_SHIELD;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAMPAGE;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAMPAGE_LEECH;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAMPAGE_CHAIN;
   @Expose
   public AbilitiesVignetteConfig.VignetteData RAGE;

   @Override
   public String getName() {
      return "abilities_vignette";
   }

   @Override
   protected void reset() {
      this.ENABLED = true;
      this.GHOST_WALK = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#ABEABE"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.TANK = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.TANK_PROJECTILE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.TANK_REFLECT = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FFFFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.EXECUTE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.MANA_SHIELD = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#00FFFF"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAMPAGE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAMPAGE_LEECH = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAMPAGE_CHAIN = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.125F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
      this.RAGE = new AbilitiesVignetteConfig.VignetteData(TextColor.parseColor("#FF0000"), 0.5F, AbilitiesVignetteConfig.VignetteStyle.VIGNETTE);
   }

   @Override
   protected boolean isValid() {
      return this.getAll().noneMatch(vignetteData -> vignetteData.color == null);
   }

   protected Stream<AbilitiesVignetteConfig.VignetteData> getAll() {
      return Stream.of(
         this.GHOST_WALK,
         this.TANK,
         this.TANK_PROJECTILE,
         this.TANK_REFLECT,
         this.EXECUTE,
         this.MANA_SHIELD,
         this.RAMPAGE,
         this.RAMPAGE_LEECH,
         this.RAMPAGE_CHAIN,
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
