package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.ability.AbilityCooldownFlatAttribute;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModEtchings;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class CooldownHelper {
   public static int adjustCooldown(ServerPlayer player, AbilityNode<?, ?> abilityNode) {
      AbstractAbilityConfig abilityConfig = abilityNode.getAbilityConfig();
      if (abilityConfig == null) {
         return 0;
      } else {
         int cooldownTicks = abilityConfig.getCooldownTicks();
         return adjustCooldown(player, abilityNode.getName(), cooldownTicks);
      }
   }

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

   public static float getCooldownMultiplier(ServerPlayer player) {
      return Mth.clamp(getCooldownMultiplierUnlimited(player), 0.0F, AttributeLimitHelper.getCooldownReductionLimit(player));
   }

   private static float getCooldownMultiplierUnlimited(ServerPlayer player) {
      float multiplier = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      multiplier += snapshot.getAttributeValue(ModGearAttributes.COOLDOWN_REDUCTION, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.COOLDOWN_REDUCTION, player, multiplier).getValue();
   }
}
