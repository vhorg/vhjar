package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class LeechHelper {
   public static float getLeechPercent(LivingEntity entity) {
      float leech = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      leech += snapshot.getAttributeValue(ModGearAttributes.LEECH, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.LEECH, entity, leech).getValue();
   }
}
