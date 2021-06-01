package iskallia.vault.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class ExecuteEffect extends Effect {
   public ExecuteEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }
}
