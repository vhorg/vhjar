package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.DashBuffConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DashBuffAbility extends DashAbility<DashBuffConfig> {
   private static final Set<UUID> dashingPlayers = new HashSet<>();

   public boolean onAction(DashBuffConfig config, PlayerEntity player, boolean active) {
      if (super.onAction(config, player, active)) {
         World world = player.func_130014_f_();
         if (world instanceof ServerWorld && player instanceof ServerPlayerEntity && dashingPlayers.add(player.func_110124_au())) {
            MinecraftServer srv = world.func_73046_m();
            AttributeModifier mod = new AttributeModifier("Dash damage increase", config.getDamageIncreasePerDash(), Operation.MULTIPLY_BASE);
            player.func_110148_a(Attributes.field_233823_f_).func_233767_b_(mod);
            ServerScheduler.INSTANCE.schedule(config.getDamageIncreaseTickTime(), () -> {
               player.func_110148_a(Attributes.field_233823_f_).func_111124_b(mod);
               dashingPlayers.remove(player.func_110124_au());
               PlayerAbilitiesData data = PlayerAbilitiesData.get(srv);
               AbilityTree abilities = data.getAbilities(player);
               AbilityNode<?, ?> dash = abilities.getNodeByName("Dash");
               abilities.putOnCooldown(player.func_184102_h(), dash, ModConfigs.ABILITIES.getCooldown(dash, (ServerPlayerEntity)player));
            });
         }
      }

      return false;
   }
}
