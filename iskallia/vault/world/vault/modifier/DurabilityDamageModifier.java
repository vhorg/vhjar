package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import net.minecraft.util.ResourceLocation;

public class DurabilityDamageModifier extends TexturedVaultModifier {
   @Expose
   protected float durabilityDamageTakenMultiplier;

   public DurabilityDamageModifier(String name, ResourceLocation icon, float durabilityDamageTakenMultiplier) {
      super(name, icon);
      this.durabilityDamageTakenMultiplier = durabilityDamageTakenMultiplier;
   }

   public float getDurabilityDamageTakenMultiplier() {
      return this.durabilityDamageTakenMultiplier;
   }
}
