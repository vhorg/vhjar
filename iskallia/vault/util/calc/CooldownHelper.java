package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.ability.AbilityCooldownFlatAttribute;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModEtchings;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class CooldownHelper {
   public static int adjustCooldown(ServerPlayer player, String ability, int cooldownTicks) {
      float cooldownMultiplier = getCooldownMultiplier(player);
      float cooldown = cooldownTicks;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);

      for (AbilityCooldownFlatAttribute attribute : snapshot.getAttributeValue(ModGearAttributes.ABILITY_COOLDOWN_FLAT, VaultGearAttributeTypeMerger.asList())) {
         cooldown = attribute.adjustCooldown(ability, cooldown);
      }

      cooldown -= cooldownTicks * cooldownMultiplier;

      for (AbilityCooldownPercentAttribute attribute : snapshot.getAttributeValue(
         ModGearAttributes.ABILITY_COOLDOWN_PERCENT, VaultGearAttributeTypeMerger.asList()
      )) {
         cooldown = attribute.adjustCooldown(ability, cooldown);
      }

      if (snapshot.hasEtching(ModEtchings.RIFT)) {
         cooldown *= ModEtchings.RIFT.getCooldownMultiplier();
      }

      return Math.max(Mth.floor(cooldown), 0);
   }

   public static float getCooldownMultiplier(LivingEntity player) {
      return Mth.clamp(getCooldownMultiplierUnlimited(player), 0.0F, AttributeLimitHelper.getCooldownReductionLimit(player));
   }

   public static float getCooldownMultiplierUnlimited(LivingEntity player) {
      float multiplier = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      multiplier += snapshot.getAttributeValue(ModGearAttributes.COOLDOWN_REDUCTION, VaultGearAttributeTypeMerger.floatSum());
      multiplier += snapshot.getAttributeValue(ModGearAttributes.COOLDOWN_REDUCTION_PERCENTILE, VaultGearAttributeTypeMerger.floatSum()) * multiplier;
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.COOLDOWN_REDUCTION, player, multiplier).getValue();
   }
}
