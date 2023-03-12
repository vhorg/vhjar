package iskallia.vault.util.calc;

import iskallia.vault.gear.attribute.ability.AbilityManaCostFlatAttribute;
import iskallia.vault.gear.attribute.ability.AbilityManaCostPercentAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.server.level.ServerPlayer;

public class ManaCostHelper {
   public static float adjustManaCost(ServerPlayer player, String abilityName, float cost) {
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);

      for (AbilityManaCostFlatAttribute attribute : snapshot.getAttributeValue(ModGearAttributes.ABILITY_MANACOST_FLAT, VaultGearAttributeTypeMerger.asList())) {
         cost = attribute.adjustManaCost(abilityName, cost);
      }

      for (AbilityManaCostPercentAttribute attribute : snapshot.getAttributeValue(
         ModGearAttributes.ABILITY_MANACOST_PERCENT, VaultGearAttributeTypeMerger.asList()
      )) {
         cost = attribute.adjustManaCost(abilityName, cost);
      }

      return cost;
   }
}
