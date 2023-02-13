package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class DurabilityWearReductionHelper {
   public static float getDurabilityWearReduction(LivingEntity entity) {
      float result = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      result += snapshot.getAttributeValue(ModGearAttributes.DURABILITY_WEAR_REDUCTION, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.DURABILITY_WEAR_REDUCTION, entity, result).getValue();
   }

   private DurabilityWearReductionHelper() {
   }
}
