package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class WeaknessEffect extends MobEffect {
   private final double multiplier;

   public WeaknessEffect(ResourceLocation id, MobEffectCategory category, int color, double multiplier) {
      super(category, color);
      this.multiplier = multiplier;
      this.setRegistryName(id);
   }

   public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
      return this.multiplier * (amplifier + 1);
   }
}
