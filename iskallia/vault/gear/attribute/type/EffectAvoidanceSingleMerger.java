package iskallia.vault.gear.attribute.type;

import iskallia.vault.gear.attribute.custom.effect.IEffectAvoidanceChanceAttribute;
import net.minecraft.world.effect.MobEffect;

public class EffectAvoidanceSingleMerger<T extends IEffectAvoidanceChanceAttribute> extends VaultGearAttributeTypeMerger<T, Float> {
   private final MobEffect effect;

   private EffectAvoidanceSingleMerger(MobEffect effect) {
      this.effect = effect;
   }

   public static <T extends IEffectAvoidanceChanceAttribute> EffectAvoidanceSingleMerger<T> of(MobEffect effect) {
      return new EffectAvoidanceSingleMerger<>(effect);
   }

   public Float merge(Float merged, T other) {
      return !other.mayAvoid(this.effect) ? merged : merged + other.getChance();
   }

   public Float getBaseValue() {
      return 0.0F;
   }
}
