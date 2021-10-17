package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import net.minecraft.util.ResourceLocation;

public class StatModifier extends TexturedVaultModifier {
   @Expose
   protected StatModifier.Statistic stat;
   @Expose
   protected float multiplier;

   public StatModifier(String name, ResourceLocation icon, StatModifier.Statistic stat, float multiplier) {
      super(name, icon);
      this.stat = stat;
      this.multiplier = multiplier;
   }

   public StatModifier.Statistic getStat() {
      return this.stat;
   }

   public float getMultiplier() {
      return this.multiplier;
   }

   public static enum Statistic {
      PARRY,
      RESISTANCE,
      COOLDOWN_REDUCTION;
   }
}
