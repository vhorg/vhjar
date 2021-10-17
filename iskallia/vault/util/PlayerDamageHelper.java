package iskallia.vault.util;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.PlayerDamageMultiplierMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class PlayerDamageHelper {
   private static final Map<UUID, Set<PlayerDamageHelper.DamageMultiplier>> multipliers = new HashMap<>();

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(ServerPlayerEntity player, float value, PlayerDamageHelper.Operation operation) {
      return applyMultiplier(player, value, operation, true);
   }

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(
      ServerPlayerEntity player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient
   ) {
      return applyMultiplier(player, value, operation, showOnClient, Integer.MAX_VALUE);
   }

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(
      ServerPlayerEntity player, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, int tickDuration
   ) {
      return applyMultiplier(player, value, operation, showOnClient, tickDuration, sPlayer -> {});
   }

   public static PlayerDamageHelper.DamageMultiplier applyMultiplier(
      ServerPlayerEntity player,
      float value,
      PlayerDamageHelper.Operation operation,
      boolean showOnClient,
      int tickDuration,
      Consumer<ServerPlayerEntity> onTimeout
   ) {
      return applyMultiplier(
         player.func_184102_h(),
         player.func_110124_au(),
         new PlayerDamageHelper.DamageMultiplier(player.func_110124_au(), value, operation, showOnClient, tickDuration, onTimeout)
      );
   }

   private static PlayerDamageHelper.DamageMultiplier applyMultiplier(MinecraftServer srv, UUID playerId, PlayerDamageHelper.DamageMultiplier multiplier) {
      multipliers.computeIfAbsent(playerId, id -> new HashSet<>()).add(multiplier);
      multiplier.removed = false;
      sync(srv, playerId);
      return multiplier;
   }

   public static boolean removeMultiplier(ServerPlayerEntity player, PlayerDamageHelper.DamageMultiplier multiplier) {
      return removeMultiplier(player.func_184102_h(), player.func_110124_au(), multiplier);
   }

   public static boolean removeMultiplier(MinecraftServer srv, UUID playerId, PlayerDamageHelper.DamageMultiplier multiplier) {
      boolean removed = multipliers.getOrDefault(playerId, new HashSet<>()).remove(multiplier);
      if (removed) {
         multiplier.removed = true;
         sync(srv, playerId);
      }

      return removed;
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase != Phase.START) {
         MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
         multipliers.forEach((playerId, multipliers) -> {
            ServerPlayerEntity sPlayer = srv.func_184103_al().func_177451_a(playerId);
            if (sPlayer != null) {
               boolean didRemoveAny = multipliers.removeIf(multiplier -> {
                  multiplier.tick();
                  if (multiplier.shouldRemove()) {
                     multiplier.onTimeout.accept(sPlayer);
                     multiplier.removed = true;
                     return true;
                  } else {
                     return false;
                  }
               });
               if (didRemoveAny) {
                  sync(srv, playerId);
               }
            }
         });
      }
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      Entity source = event.getSource().func_76346_g();
      if (source instanceof ServerPlayerEntity) {
         event.setAmount(event.getAmount() * getDamageMultiplier((ServerPlayerEntity)source, true));
      }
   }

   public static float getDamageMultiplier(PlayerEntity player, boolean ignoreClientFlag) {
      return getDamageMultiplier(player.func_110124_au(), ignoreClientFlag);
   }

   private static float getDamageMultiplier(UUID playerId, boolean ignoreClientFlag) {
      Set<PlayerDamageHelper.DamageMultiplier> damageMultipliers = multipliers.getOrDefault(playerId, new HashSet<>());
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
      ServerPlayerEntity sPlayer = srv.func_184103_al().func_177451_a(playerId);
      if (sPlayer != null) {
         sync(sPlayer);
      }
   }

   public static void sync(ServerPlayerEntity sPlayer) {
      float multiplier = getDamageMultiplier(sPlayer, false);
      ModNetwork.CHANNEL.sendTo(new PlayerDamageMultiplierMessage(multiplier), sPlayer.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static class DamageMultiplier {
      private static int counter = 0;
      private final int id;
      private final UUID playerId;
      private final float value;
      private final PlayerDamageHelper.Operation operation;
      private final boolean showOnClient;
      private final int originalTimeout;
      private int tickTimeout;
      private final Consumer<ServerPlayerEntity> onTimeout;
      private boolean removed = false;

      private DamageMultiplier(
         UUID playerId, float value, PlayerDamageHelper.Operation operation, boolean showOnClient, int tickTimeout, Consumer<ServerPlayerEntity> onTimeout
      ) {
         this.id = counter++;
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
            PlayerDamageHelper.applyMultiplier(srv, this.playerId, this);
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
            return this.id == that.id;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.id;
      }
   }

   public static enum Operation {
      ADDITIVE_MULTIPLY,
      STACKING_MULTIPLY;
   }
}
