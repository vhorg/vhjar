package iskallia.vault.gear.attribute.type;

import iskallia.vault.gear.attribute.custom.EffectAvoidanceGearAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.effect.MobEffect;

public class EffectAvoidanceCombinedMerger extends VaultGearAttributeTypeMerger<EffectAvoidanceGearAttribute, EffectAvoidanceCombinedMerger.Avoidances> {
   private static final EffectAvoidanceCombinedMerger INSTANCE = new EffectAvoidanceCombinedMerger();

   private EffectAvoidanceCombinedMerger() {
   }

   public static EffectAvoidanceCombinedMerger getInstance() {
      return INSTANCE;
   }

   public EffectAvoidanceCombinedMerger.Avoidances merge(EffectAvoidanceCombinedMerger.Avoidances merged, EffectAvoidanceGearAttribute other) {
      if (other.getChance() <= 0.0F) {
         return merged;
      } else {
         float chance = merged.avoidanceChances.getOrDefault(other.getEffect(), 0.0F);
         merged.avoidanceChances.put(other.getEffect(), chance + other.getChance());
         return merged;
      }
   }

   public EffectAvoidanceCombinedMerger.Avoidances getBaseValue() {
      return new EffectAvoidanceCombinedMerger.Avoidances();
   }

   public static class Avoidances {
      private final Map<MobEffect, Float> avoidanceChances = new HashMap<>();

      public Map<MobEffect, Float> getAvoidanceChances() {
         return Collections.unmodifiableMap(this.avoidanceChances);
      }
   }
}
