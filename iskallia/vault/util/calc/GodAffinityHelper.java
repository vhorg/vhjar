package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;

public class GodAffinityHelper {
   private static final Map<VaultGod, VaultGearAttribute<Float>> GOD_TO_ATTRIBUTE = new HashMap<>();
   private static final Map<VaultGod, PlayerStat> GOD_TO_STAT = new HashMap<>();

   public static float getAffinityPercent(LivingEntity entity, VaultGod god) {
      float affinity = 0.05F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      affinity += snapshot.getAttributeValue(GOD_TO_ATTRIBUTE.get(god), VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(GOD_TO_STAT.get(god), entity, affinity).getValue();
   }

   static {
      GOD_TO_ATTRIBUTE.put(VaultGod.VELARA, ModGearAttributes.VELARA_AFFINITY);
      GOD_TO_ATTRIBUTE.put(VaultGod.TENOS, ModGearAttributes.TENOS_AFFINITY);
      GOD_TO_ATTRIBUTE.put(VaultGod.WENDARR, ModGearAttributes.WENDARR_AFFINITY);
      GOD_TO_ATTRIBUTE.put(VaultGod.IDONA, ModGearAttributes.IDONA_AFFINITY);
      GOD_TO_STAT.put(VaultGod.VELARA, PlayerStat.VELARA_AFFINITY);
      GOD_TO_STAT.put(VaultGod.TENOS, PlayerStat.TENOS_AFFINITY);
      GOD_TO_STAT.put(VaultGod.WENDARR, PlayerStat.WENDARR_AFFINITY);
      GOD_TO_STAT.put(VaultGod.IDONA, PlayerStat.IDONA_AFFINITY);
   }
}
