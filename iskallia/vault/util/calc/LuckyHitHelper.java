package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class LuckyHitHelper {
   public static float getLuckyHitChance(LivingEntity entity) {
      float chance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      chance += snapshot.getAttributeValue(ModGearAttributes.LUCKY_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.LUCKY_HIT_CHANCE, entity, chance).getValue();
   }
}
