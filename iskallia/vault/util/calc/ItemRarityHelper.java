package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;

public final class ItemRarityHelper {
   public static float getItemRarity(LivingEntity entity) {
      float result = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      result += snapshot.getAttributeValue(ModGearAttributes.ITEM_RARITY, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.ITEM_RARITY, entity, result).getValue();
   }

   private ItemRarityHelper() {
   }
}
