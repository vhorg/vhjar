package iskallia.vault.util.calc;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class AreaOfEffectHelper {
   public static float adjustAreaOfEffect(LivingEntity entity, float range) {
      float multiplier = 1.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      multiplier += snapshot.getAttributeValue(ModGearAttributes.AREA_OF_EFFECT, VaultGearAttributeTypeMerger.floatSum());
      return range * multiplier;
   }
}
