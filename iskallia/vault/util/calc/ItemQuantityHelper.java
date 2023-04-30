package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.VaultLoot;
import net.minecraft.world.entity.LivingEntity;

public final class ItemQuantityHelper {
   public static float getItemQuantity(LivingEntity entity) {
      float result = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      result += snapshot.getAttributeValue(ModGearAttributes.ITEM_QUANTITY, VaultGearAttributeTypeMerger.floatSum());
      result = CommonEvents.PLAYER_STAT.invoke(PlayerStat.ITEM_QUANTITY, entity, result).getValue();
      result *= ((VaultLoot.GameRuleValue)entity.level.getGameRules().getRule(ModGameRules.LOOT)).get().getMultiplier();
      return result + (((VaultLoot.GameRuleValue)entity.level.getGameRules().getRule(ModGameRules.LOOT)).get().getMultiplier() - 1);
   }

   private ItemQuantityHelper() {
   }
}
