package iskallia.vault.util.calc;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class AttributeLimitHelper {
   public static float getCooldownReductionLimit(LivingEntity entity) {
      float limit = 0.8F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      limit += snapshot.getAttributeValue(ModGearAttributes.COOLDOWN_REDUCTION_CAP, VaultGearAttributeTypeMerger.floatSum());
      return Mth.clamp(limit, 0.0F, 0.95F);
   }

   public static float getBlockChanceLimit(LivingEntity entity) {
      float limit = 0.6F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      limit += snapshot.getAttributeValue(ModGearAttributes.BLOCK_CAP, VaultGearAttributeTypeMerger.floatSum());
      return Mth.clamp(limit, 0.0F, 0.95F);
   }

   public static float getResistanceLimit(LivingEntity entity) {
      float limit = 0.5F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      limit += snapshot.getAttributeValue(ModGearAttributes.RESISTANCE_CAP, VaultGearAttributeTypeMerger.floatSum());
      return Mth.clamp(limit, 0.0F, 0.95F);
   }
}
