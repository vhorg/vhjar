package iskallia.vault.init;

import iskallia.vault.mixin.MixinBooleanValue;
import iskallia.vault.mixin.MixinIntegerValue;
import iskallia.vault.network.message.ClientboundSyncVaultAllowWaypointsMessage;
import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber
public class ModGameRules {
   public static Key<BooleanValue> FINAL_VAULT_ALLOW_PARTY;
   public static Key<BooleanValue> VAULT_JOIN_REQUIRE_PARTY;
   public static Key<IntegerValue> VAULT_TEMPLATE_CACHE_SIZE;
   public static Key<BooleanValue> CASUAL_VAULTS;
   public static Key<BooleanValue> VAULT_ALLOW_WAYPOINTS;
   public static Key<BooleanValue> VAULT_NO_OP_DIFFICULTY;

   public static void initialize() {
      FINAL_VAULT_ALLOW_PARTY = register("finalVaultAllowParty", Category.MISC, booleanRule(true));
      VAULT_JOIN_REQUIRE_PARTY = register("vaultJoinRequireParty", Category.MISC, booleanRule(true));
      VAULT_TEMPLATE_CACHE_SIZE = register("vaultTemplateCacheSize", Category.MISC, integerRule(16));
      CASUAL_VAULTS = register("vaultCasualMode", Category.MISC, booleanRule(false));
      VAULT_ALLOW_WAYPOINTS = register(
         "vaultAllowWaypoints",
         Category.MISC,
         booleanRule(
            false, (server, value) -> ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundSyncVaultAllowWaypointsMessage(value.get()))
         )
      );
      VAULT_NO_OP_DIFFICULTY = register("vaultNoOpDifficulty", Category.MISC, booleanRule(true));
   }

   public static <T extends Value<T>> Key<T> register(String name, Category category, Type<T> type) {
      return GameRules.register(name, category, type);
   }

   public static Type<BooleanValue> booleanRule(boolean defaultValue) {
      return MixinBooleanValue.create(defaultValue, (minecraftServer, booleanValue) -> {});
   }

   public static Type<BooleanValue> booleanRule(boolean defaultValue, BiConsumer<MinecraftServer, BooleanValue> changeListener) {
      return MixinBooleanValue.create(defaultValue, changeListener);
   }

   public static Type<IntegerValue> integerRule(int defaultValue) {
      return MixinIntegerValue.create(defaultValue);
   }

   @SubscribeEvent
   public static void syncGameRules(OnDatapackSyncEvent event) {
      ServerPlayer player = event.getPlayer();
      if (player == null) {
         event.getPlayerList()
            .getPlayers()
            .forEach(
               serverPlayer -> ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.PLAYER.with(() -> serverPlayer),
                     new ClientboundSyncVaultAllowWaypointsMessage(serverPlayer.getLevel().getGameRules().getBoolean(VAULT_ALLOW_WAYPOINTS))
                  )
            );
      } else {
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.PLAYER.with(() -> player),
               new ClientboundSyncVaultAllowWaypointsMessage(player.getLevel().getGameRules().getBoolean(VAULT_ALLOW_WAYPOINTS))
            );
      }
   }

   @SubscribeEvent
   public static void syncGameRulesOnDimensionChange(PlayerChangedDimensionEvent event) {
      if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.PLAYER.with(() -> serverPlayer),
               new ClientboundSyncVaultAllowWaypointsMessage(serverPlayer.getLevel().getGameRules().getBoolean(VAULT_ALLOW_WAYPOINTS))
            );
      }
   }
}
