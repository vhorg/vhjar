package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BasicEffect extends MobEffect {
   public BasicEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean isDurationEffectTick(int duration, int amplifier) {
      return true;
   }
}
