package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class ChainHelper {
   public static int getChainCount(LivingEntity attacker) {
      int chainCount = 0;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
      chainCount += snapshot.getAttributeValue(ModGearAttributes.ON_HIT_CHAIN, VaultGearAttributeTypeMerger.intSum());
      return Math.round(CommonEvents.PLAYER_STAT.invoke(PlayerStat.ON_HIT_CHAIN, attacker, chainCount).getValue());
   }
}
