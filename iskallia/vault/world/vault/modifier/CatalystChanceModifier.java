package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import net.minecraft.util.ResourceLocation;

public class CatalystChanceModifier extends TexturedVaultModifier {
   @Expose
   private final float catalystChanceIncrease;

   public CatalystChanceModifier(String name, ResourceLocation icon, float chanceIncrease) {
      super(name, icon);
      this.catalystChanceIncrease = chanceIncrease;
   }

   public float getCatalystChanceIncrease() {
      return this.catalystChanceIncrease;
   }
}
