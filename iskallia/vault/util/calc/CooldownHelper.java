package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModEtchings;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.group.AbilityGroup;
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
         AbilityGroup<?, ?> abilityGroup = abilityNode.getGroup();
         return adjustCooldown(player, cooldownTicks);
      }
   }

   public static int adjustCooldown(ServerPlayer player, int cooldownTicks) {
      float cooldownMultiplier = getCooldownMultiplier(player);
      cooldownTicks -= Mth.floor(cooldownTicks * cooldownMultiplier);
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      if (snapshot.hasEtching(ModEtchings.RIFT)) {
         cooldownTicks = (int)(cooldownTicks * ModEtchings.RIFT.getCooldownMultiplier());
      }

      return cooldownTicks;
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
