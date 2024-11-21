package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModAttributes;
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

   public static float getDurabilityWearReductionLimit(LivingEntity entity) {
      float limit = 0.5F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      limit += snapshot.getAttributeValue(ModGearAttributes.DURABILITY_WEAR_REDUCTION_CAP, VaultGearAttributeTypeMerger.floatSum());
      limit = (float)(limit + entity.getAttributeValue(ModAttributes.DURABILITY_WEAR_REDUCTION_CAP));
      limit = CommonEvents.PLAYER_STAT.invoke(PlayerStat.DURABILITY_WEAR_REDUCTION_CAP, entity, limit).getValue();
      return Mth.clamp(limit, 0.0F, 1.0F);
   }
}
