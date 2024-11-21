package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class SoulChanceHelper {
   public static float getSoulChance(LivingEntity attacker) {
      return getSoulChance(attacker, null);
   }

   public static float getSoulChance(LivingEntity attacker, LivingEntity attacked) {
      float soulChance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
      soulChance += snapshot.getAttributeValue(ModGearAttributes.SOUL_QUANTITY, VaultGearAttributeTypeMerger.floatSum());
      soulChance += snapshot.getAttributeValue(ModGearAttributes.SOUL_QUANTITY_PERCENTILE, VaultGearAttributeTypeMerger.floatSum()) * soulChance;
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.SOUL_CHANCE, attacker, soulChance, data -> data.setEnemy(attacked)).getValue();
   }
}
