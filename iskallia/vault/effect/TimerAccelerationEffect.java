package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TimerAccelerationEffect extends MobEffect {
   public TimerAccelerationEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean isInstantenous() {
      return false;
   }
}
