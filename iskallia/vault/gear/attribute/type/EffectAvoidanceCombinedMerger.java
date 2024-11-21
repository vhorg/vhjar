package iskallia.vault.gear.attribute.type;

import iskallia.vault.gear.attribute.custom.effect.IEffectAvoidanceChanceAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.effect.MobEffect;

public class EffectAvoidanceCombinedMerger<T extends IEffectAvoidanceChanceAttribute>
   extends VaultGearAttributeTypeMerger<T, EffectAvoidanceCombinedMerger.Avoidances> {
   private EffectAvoidanceCombinedMerger() {
   }

   public static <T extends IEffectAvoidanceChanceAttribute> EffectAvoidanceCombinedMerger<T> of() {
      return new EffectAvoidanceCombinedMerger<>();
   }

   public EffectAvoidanceCombinedMerger.Avoidances merge(EffectAvoidanceCombinedMerger.Avoidances merged, IEffectAvoidanceChanceAttribute other) {
      if (other.getChance() <= 0.0F) {
         return merged;
      } else {
         other.getEffects().forEach(effect -> {
            float chance = merged.avoidanceChances.getOrDefault(effect, 0.0F);
            merged.avoidanceChances.put(effect, chance + other.getChance());
         });
         return merged;
      }
   }

   public EffectAvoidanceCombinedMerger.Avoidances getBaseValue() {
      return new EffectAvoidanceCombinedMerger.Avoidances();
   }

   public static class Avoidances {
      private final Map<MobEffect, Float> avoidanceChances = new HashMap<>();

      public static EffectAvoidanceCombinedMerger.Avoidances empty() {
         return new EffectAvoidanceCombinedMerger.Avoidances();
      }

      public void merge(EffectAvoidanceCombinedMerger.Avoidances other) {
         other.avoidanceChances.forEach((effect, chance) -> {
            float currentChance = this.avoidanceChances.getOrDefault(effect, 0.0F);
            this.avoidanceChances.put(effect, currentChance + chance);
         });
      }

      public Map<MobEffect, Float> getAvoidanceChances() {
         return Collections.unmodifiableMap(this.avoidanceChances);
      }
   }
}
