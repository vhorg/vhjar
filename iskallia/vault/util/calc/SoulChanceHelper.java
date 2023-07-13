package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class SoulChanceHelper {
   public static float getSoulChance(LivingEntity entity) {
      float soulChance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      soulChance += snapshot.getAttributeValue(ModGearAttributes.SOUL_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      soulChance += snapshot.getAttributeValue(ModGearAttributes.SOUL_CHANCE_PERCENTILE, VaultGearAttributeTypeMerger.floatSum()) * soulChance;
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.SOUL_CHANCE, entity, soulChance).getValue();
   }
}
