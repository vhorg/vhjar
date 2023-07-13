package iskallia.vault.skill.ability.cooldown;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityCooldownMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class AbilityCooldownManager {
   private static final Map<String, CooldownInstance> CLIENT_COOLDOWNS = new HashMap<>();
   private static final Map<UUID, Map<String, CooldownInstance>> PLAYER_COOLDOWNS = new HashMap<>();

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv != null) {
            PLAYER_COOLDOWNS.forEach(
               (uuid, cooldowns) -> {
                  boolean update = !cooldowns.isEmpty();
                  Iterator<Entry<String, CooldownInstance>> iterator = cooldowns.entrySet().iterator();

                  while (iterator.hasNext()) {
                     Entry<String, CooldownInstance> entry = iterator.next();
                     CooldownInstance cooldown = entry.getValue();
                     cooldown.decrement();
                     if (cooldown.getRemainingTicks() <= 0) {
                        iterator.remove();
                     }
                  }

                  if (update) {
                     ServerPlayer player = srv.getPlayerList().getPlayer(uuid);
                     if (player != null) {
                        ModNetwork.CHANNEL
                           .sendTo(
                              new AbilityCooldownMessage((Map<String, CooldownInstance>)cooldowns),
                              player.connection.getConnection(),
                              NetworkDirection.PLAY_TO_CLIENT
                           );
                     }
                  }
               }
            );
         }
      }
   }

   public static CooldownInstance getCooldown(Player player, String abilityGroup) {
      return player.getLevel().isClientSide()
         ? CLIENT_COOLDOWNS.getOrDefault(abilityGroup, CooldownInstance.EMPTY)
         : PLAYER_COOLDOWNS.getOrDefault(player.getUUID(), Collections.emptyMap()).getOrDefault(abilityGroup, CooldownInstance.EMPTY);
   }

   public static boolean isOnCooldown(Player player, Ability ability) {
      return isOnCooldown(player, ability.getAbilityGroupName());
   }

   public static boolean isOnCooldown(Player player, String abilityGroup) {
      return player.getLevel().isClientSide()
         ? CLIENT_COOLDOWNS.getOrDefault(abilityGroup, CooldownInstance.EMPTY).getRemainingTicks() > 0
         : PLAYER_COOLDOWNS.getOrDefault(player.getUUID(), Collections.emptyMap()).getOrDefault(abilityGroup, CooldownInstance.EMPTY).getRemainingTicks() > 0;
   }

   public static void putOnCooldown(Player player, String abilityGroup, int cooldown, int cooldownDelay) {
      if (!player.getLevel().isClientSide()) {
         PLAYER_COOLDOWNS.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>()).put(abilityGroup, new CooldownInstance(cooldown, cooldown, cooldownDelay));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void updateClientCooldown(Map<String, CooldownInstance> cooldowns) {
      CLIENT_COOLDOWNS.clear();
      CLIENT_COOLDOWNS.putAll(cooldowns);
   }
}
