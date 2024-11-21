package iskallia.vault.gear.attribute.type;

import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.effect.MobEffect;

public class EffectAttributeMerger extends VaultGearAttributeTypeMerger<EffectGearAttribute, EffectAttributeMerger.CombinedEffects> {
   private static final EffectAttributeMerger INSTANCE = new EffectAttributeMerger();

   private EffectAttributeMerger() {
   }

   public static EffectAttributeMerger getInstance() {
      return INSTANCE;
   }

   public EffectAttributeMerger.CombinedEffects merge(EffectAttributeMerger.CombinedEffects merged, EffectGearAttribute other) {
      if (other.getAmplifier() <= 0) {
         return merged;
      } else {
         int level = merged.levelMap.getOrDefault(other.getEffect(), 0);
         merged.levelMap.put(other.getEffect(), level + other.getAmplifier());
         return merged;
      }
   }

   public EffectAttributeMerger.CombinedEffects getBaseValue() {
      return new EffectAttributeMerger.CombinedEffects();
   }

   public static class CombinedEffects {
      private final Map<MobEffect, Integer> levelMap = new HashMap<>();

      public Map<MobEffect, Integer> getEffects() {
         return Collections.unmodifiableMap(this.levelMap);
      }
   }
}
