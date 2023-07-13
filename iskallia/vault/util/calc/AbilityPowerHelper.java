package iskallia.vault.util.calc;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.player.Player;

public class AbilityPowerHelper {
   public static float getAbilityPower(Player player) {
      float multiplier = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      multiplier += snapshot.getAttributeValue(ModGearAttributes.ABILITY_POWER, VaultGearAttributeTypeMerger.floatSum());
      multiplier += snapshot.getAttributeValue(ModGearAttributes.ABILITY_POWER_PERCENT, VaultGearAttributeTypeMerger.floatSum()) * multiplier;
      return multiplier + snapshot.getAttributeValue(ModGearAttributes.ABILITY_POWER_PERCENTILE, VaultGearAttributeTypeMerger.floatSum()) * multiplier;
   }
}
