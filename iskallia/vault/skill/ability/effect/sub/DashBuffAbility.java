package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.DashBuffConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DashBuffAbility extends DashAbility<DashBuffConfig> {
   private static final Set<UUID> dashingPlayers = new HashSet<>();

   public boolean onAction(DashBuffConfig config, ServerPlayerEntity player, boolean active) {
      World world = player.func_130014_f_();
      if (world instanceof ServerWorld && dashingPlayers.add(player.func_110124_au()) && super.onAction(config, player, active)) {
         float dmgIncrease = config.getDamageIncreasePerDash();
         int tickTime = config.getDamageIncreaseTickTime();
         PlayerDamageHelper.applyMultiplier(player, dmgIncrease, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY, true, tickTime, sPlayer -> {
            dashingPlayers.remove(player.func_110124_au());
            PlayerAbilitiesData.setAbilityOnCooldown(player, "Dash");
         });
      }

      return false;
   }
}
