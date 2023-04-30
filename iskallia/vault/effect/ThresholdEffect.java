package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ThresholdEffect extends MobEffect {
   public ThresholdEffect(int color, ResourceLocation id) {
      super(MobEffectCategory.BENEFICIAL, color);
      this.setRegistryName(id);
   }
}
