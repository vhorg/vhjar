package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class FatalStrikeHelper {
   @Deprecated(
      forRemoval = true
   )
   public static float getPlayerFatalStrikeChance(ServerPlayer player) {
      float chance = 0.0F;
      return chance + getFatalStrikeChance(player);
   }

   public static float getFatalStrikeChance(LivingEntity entity) {
      float chance = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      chance += snapshot.getAttributeValue(ModGearAttributes.FATAL_STRIKE_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.FATAL_STRIKE_CHANCE, entity, chance).getValue();
   }

   @Deprecated(
      forRemoval = true
   )
   public static float getPlayerFatalStrikeDamage(ServerPlayer player) {
      float additionalMultiplier = 0.0F;
      return additionalMultiplier + getFatalStrikeDamage(player);
   }

   public static float getFatalStrikeDamage(LivingEntity entity) {
      float additionalMultiplier = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      additionalMultiplier += snapshot.getAttributeValue(ModGearAttributes.FATAL_STRIKE_DAMAGE, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.FATAL_STRIKE_DAMAGE, entity, additionalMultiplier).getValue();
   }
}
