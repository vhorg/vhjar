package iskallia.vault.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerActiveFlags {
   private static final Map<UUID, List<PlayerActiveFlags.FlagTimeout>> timeouts = new HashMap<>();

   @SubscribeEvent
   public static void onTick(ServerTickEvent event) {
      if (event.phase != Phase.END) {
         timeouts.forEach((playerId, flagTimeouts) -> {
            flagTimeouts.forEach(rec$ -> rec$.tick());
            flagTimeouts.removeIf(rec$ -> rec$.isFinished());
         });
      }
   }

   public static void set(PlayerEntity player, PlayerActiveFlags.Flag flag, int timeout) {
      set(player.func_110124_au(), flag, timeout);
   }

   public static void set(UUID playerId, PlayerActiveFlags.Flag flag, int timeout) {
      List<PlayerActiveFlags.FlagTimeout> flags = timeouts.computeIfAbsent(playerId, id -> new ArrayList<>());

      for (PlayerActiveFlags.FlagTimeout flagTimeout : flags) {
         if (flagTimeout.flag == flag) {
            flagTimeout.tickTimeout = timeout;
            return;
         }
      }

      flags.add(new PlayerActiveFlags.FlagTimeout(flag, timeout));
   }

   public static boolean isSet(PlayerEntity player, PlayerActiveFlags.Flag flag) {
      return isSet(player.func_110124_au(), flag);
   }

   public static boolean isSet(UUID playerId, PlayerActiveFlags.Flag flag) {
      for (PlayerActiveFlags.FlagTimeout timeout : timeouts.getOrDefault(playerId, Collections.emptyList())) {
         if (timeout.flag == flag && !timeout.isFinished()) {
            return true;
         }
      }

      return false;
   }

   public static enum Flag {
      ATTACK_AOE,
      CHAINING_AOE;
   }

   private static class FlagTimeout {
      private final PlayerActiveFlags.Flag flag;
      private int tickTimeout;

      private FlagTimeout(PlayerActiveFlags.Flag flag, int tickTimeout) {
         this.flag = flag;
         this.tickTimeout = tickTimeout;
      }

      private void tick() {
         this.tickTimeout--;
      }

      private boolean isFinished() {
         return this.tickTimeout <= 0;
      }
   }
}
