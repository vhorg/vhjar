package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class ThornsHelper {
   public static float getThornsChance(LivingEntity entity) {
      float chance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      chance += snapshot.getAttributeValue(ModGearAttributes.THORNS_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.THORNS_CHANCE, entity, chance).getValue();
   }

   public static float getThornsDamage(LivingEntity entity) {
      float additionalMultiplier = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      additionalMultiplier += snapshot.getAttributeValue(ModGearAttributes.THORNS_DAMAGE, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.THORNS_DAMAGE, entity, additionalMultiplier).getValue();
   }
}
