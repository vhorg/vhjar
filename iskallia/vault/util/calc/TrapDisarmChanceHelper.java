package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class TrapDisarmChanceHelper {
   public static float getTrapDisarmChance(LivingEntity entity) {
      float trapDisarmChance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      trapDisarmChance += snapshot.getAttributeValue(ModGearAttributes.TRAP_DISARMING, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.TRAP_DISARM_CHANCE, entity, trapDisarmChance).getValue();
   }
}
