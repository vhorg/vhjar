package iskallia.vault.gear.attribute.type;

import iskallia.vault.gear.attribute.custom.EffectAvoidanceGearAttribute;
import net.minecraft.world.effect.MobEffect;

public class EffectAvoidanceSingleMerger extends VaultGearAttributeTypeMerger<EffectAvoidanceGearAttribute, Float> {
   private final MobEffect effect;

   private EffectAvoidanceSingleMerger(MobEffect effect) {
      this.effect = effect;
   }

   public static EffectAvoidanceSingleMerger of(MobEffect effect) {
      return new EffectAvoidanceSingleMerger(effect);
   }

   public Float merge(Float merged, EffectAvoidanceGearAttribute other) {
      return !other.getEffect().equals(this.effect) ? merged : merged + other.getChance();
   }

   public Float getBaseValue() {
      return 0.0F;
   }
}
