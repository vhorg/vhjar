package iskallia.vault.util.damage;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.PlayerDamageMultiplierMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class PlayerDamageHelper {
   private static final Map<UUID, Map<UUID, PlayerDamageHelper.DamageMultiplier>> multipliers = new HashMap<>();

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(UUID id, ServerPlayer player, float value, PlayerDamageHelper.Operation operation) {
      return applyMultiplier(id, player, value, operation, true);
   }

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(
      UUID id, ServerPlayer player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient
   ) {
      return applyMultiplier(id, player, value, operation, showOnClient, sPlayer -> {});
   }

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(
      UUID id, ServerPlayer player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, Consumer<ServerPlayer> onTimeout
   ) {
      return apply(
         player.getServer(),
         player.getUUID(),
         new PlayerDamageHelper.DamageMultiplier(id, player.getUUID(), value, operation, showOnClient, Integer.MAX_VALUE, onTimeout)
      );
   }

   public static PlayerDamageHelper.DamageMultiplier applyTimedMultiplier(
      ServerPlayer player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, int tickDuration
   ) {
      return applyTimedMultiplier(findFreeMultiplierId(player), player, value, operation, showOnClient, tickDuration);
   }

   public static PlayerDamageHelper.DamageMultiplier applyTimedMultiplier(
      UUID id, ServerPlayer player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, int tickDuration
   ) {
      return applyTimedMultiplier(id, player, value, operation, showOnClient, tickDuration, sPlayer -> {});
   }

   public static PlayerDamageHelper.DamageMultiplier applyTimedMultiplier(
      ServerPlayer player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, int tickDuration, Consumer<ServerPlayer> onTimeout
   ) {
      return applyTimedMultiplier(findFreeMultiplierId(player), player, value, operation, showOnClient, tickDuration, onTimeout);
   }

   public static PlayerDamageHelper.DamageMultiplier applyTimedMultiplier(
      UUID id,
      ServerPlayer player,
      float value,
      PlayerDamageHelper.Operation operation,
      boolean showOnClient,
      int tickDuration,
      Consumer<ServerPlayer> onTimeout
   ) {
      return apply(
         player.getServer(),
         player.getUUID(),
         new PlayerDamageHelper.DamageMultiplier(id, player.getUUID(), value, operation, showOnClient, tickDuration, onTimeout)
      );
   }

   private static UUID findFreeMultiplierId(ServerPlayer player) {
      UUID freeId;
      do {
         freeId = UUID.randomUUID();
      } while (getMultiplier(player, freeId) != null);

      return freeId;
   }

   private static PlayerDamageHelper.DamageMultiplier apply(MinecraftServer srv, UUID playerId, PlayerDamageHelper.DamageMultiplier multiplier) {
      multipliers.computeIfAbsent(playerId, id -> new HashMap<>()).put(multiplier.id, multiplier);
      multiplier.removed = false;
      sync(srv, playerId);
      return multiplier;
   }

   public static boolean hasMultiplier(ServerPlayer player, UUID id) {
      return multipliers.getOrDefault(player.getUUID(), Collections.emptyMap()).containsKey(id);
   }

   @Nullable
   public static PlayerDamageHelper.DamageMultiplier getMultiplier(ServerPlayer player, UUID id) {
      return multipliers.getOrDefault(player.getUUID(), Collections.emptyMap()).get(id);
   }

   public static boolean removeMultiplier(ServerPlayer player, PlayerDamageHelper.DamageMultiplier multiplier) {
      return removeMultiplier(player.getServer(), player.getUUID(), multiplier);
   }

   public static boolean removeMultiplier(ServerPlayer player, UUID modifierId) {
      return removeMultiplier(player.getServer(), player.getUUID(), modifierId);
   }

   public static boolean removeMultiplier(MinecraftServer srv, UUID playerId, PlayerDamageHelper.DamageMultiplier multiplier) {
      return removeMultiplier(srv, playerId, multiplier.id);
   }

   public static boolean removeMultiplier(MinecraftServer srv, UUID playerId, UUID modifierId) {
      PlayerDamageHelper.DamageMultiplier removed = multipliers.getOrDefault(playerId, Collections.emptyMap()).remove(modifierId);
      if (removed != null) {
         removed.removed = true;
         sync(srv, playerId);
      }

      return removed != null;
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase != Phase.START) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         multipliers.forEach((playerId, multipliers) -> {
            ServerPlayer sPlayer = srv.getPlayerList().getPlayer(playerId);
            if (sPlayer != null) {
               List<UUID> toRemove = new ArrayList<>();
               multipliers.forEach((id, multiplier) -> {
                  multiplier.tick();
                  if (multiplier.shouldRemove()) {
                     multiplier.onTimeout.accept(sPlayer);
                     multiplier.removed = true;
                     toRemove.add(id);
                  }
               });
               toRemove.forEach(multipliers::remove);
               if (!toRemove.isEmpty()) {
                  sync(srv, playerId);
               }
            }
         });
      }
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      Entity source = event.getSource().getEntity();
      if (source instanceof ServerPlayer && !ActiveFlags.IS_AOE_ATTACKING.isSet()) {
         event.setAmount(event.getAmount() * getDamageMultiplier((ServerPlayer)source, true));
      }
   }

   @SubscribeEvent
   public static void on(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         sync(player);
      }
   }

   @SubscribeEvent
   public static void on(PlayerLoggedOutEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         multipliers.remove(player.getUUID());
      }
   }

   public static float getDamageMultiplier(Player player, boolean ignoreClientFlag) {
      return getDamageMultiplier(player.getUUID(), ignoreClientFlag);
   }

   private static float getDamageMultiplier(UUID playerId, boolean ignoreClientFlag) {
      Collection<PlayerDamageHelper.DamageMultiplier> damageMultipliers = multipliers.getOrDefault(playerId, Collections.emptyMap()).values();
      float multiplier = 1.0F;

      for (PlayerDamageHelper.DamageMultiplier mult : damageMultipliers) {
         if ((ignoreClientFlag || mult.showOnClient) && mult.operation == PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY) {
            multiplier += mult.value;
         }
      }

      for (PlayerDamageHelper.DamageMultiplier multx : damageMultipliers) {
         if ((ignoreClientFlag || multx.showOnClient) && multx.operation == PlayerDamageHelper.Operation.STACKING_MULTIPLY) {
            multiplier *= multx.value;
         }
      }

      return Math.max(multiplier, 0.0F);
   }

   public static void syncAll(MinecraftServer srv) {
      multipliers.keySet().forEach(playerId -> sync(srv, playerId));
   }

   public static void sync(MinecraftServer srv, UUID playerId) {
      ServerPlayer sPlayer = srv.getPlayerList().getPlayer(playerId);
      if (sPlayer != null) {
         sync(sPlayer);
      }
   }

   public static void sync(ServerPlayer sPlayer) {
      float multiplier = getDamageMultiplier(sPlayer, false);
      ModNetwork.CHANNEL.sendTo(new PlayerDamageMultiplierMessage(multiplier), sPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static class DamageMultiplier {
      private final UUID id;
      private final UUID playerId;
      private final float value;
      private final PlayerDamageHelper.Operation operation;
      private final boolean showOnClient;
      private final int originalTimeout;
      private int tickTimeout;
      private final Consumer<ServerPlayer> onTimeout;
      private boolean removed = false;

      private DamageMultiplier(
         UUID id, UUID playerId, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, int tickTimeout, Consumer<ServerPlayer> onTimeout
      ) {
         this.id = id;
         this.playerId = playerId;
         this.value = value;
         this.operation = operation;
         this.showOnClient = showOnClient;
         this.originalTimeout = tickTimeout;
         this.tickTimeout = tickTimeout;
         this.onTimeout = onTimeout;
      }

      public float getMultiplier() {
         return this.value;
      }

      private boolean shouldRemove() {
         return this.tickTimeout < 0;
      }

      public void refreshDuration(MinecraftServer srv) {
         if (this.removed) {
            PlayerDamageHelper.apply(srv, this.playerId, this);
         }

         this.tickTimeout = this.originalTimeout;
      }

      private void tick() {
         if (this.tickTimeout != Integer.MAX_VALUE) {
            this.tickTimeout--;
         }
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            PlayerDamageHelper.DamageMultiplier that = (PlayerDamageHelper.DamageMultiplier)o;
            return Objects.equals(this.id, that.id);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.id);
      }
   }

   public static enum Operation {
      ADDITIVE_MULTIPLY,
      STACKING_MULTIPLY;
   }
}
