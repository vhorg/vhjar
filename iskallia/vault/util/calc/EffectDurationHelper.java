package iskallia.vault.util.calc;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class EffectDurationHelper {
   public static float adjustEffectDuration(LivingEntity entity, float duration) {
      float multiplier = 1.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      multiplier += snapshot.getAttributeValue(ModGearAttributes.EFFECT_DURATION, VaultGearAttributeTypeMerger.floatSum());
      return duration * multiplier;
   }

   public static int adjustEffectDurationFloor(LivingEntity entity, float duration) {
      return Mth.floor(adjustEffectDuration(entity, duration));
   }
}
