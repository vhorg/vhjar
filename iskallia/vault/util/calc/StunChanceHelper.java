package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public class StunChanceHelper {
   public static float getStunChance(LivingEntity attacker, LivingEntity attacked) {
      float chance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
      chance += snapshot.getAttributeValue(ModGearAttributes.ON_HIT_STUN, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.STUN_CHANCE, attacker, chance, data -> data.setEnemy(attacked)).getValue();
   }
}
