package iskallia.vault.init;

import iskallia.vault.mixin.MixinBooleanValue;
import iskallia.vault.mixin.MixinIntegerValue;
import iskallia.vault.network.message.ClientboundSyncVaultAllowWaypointsMessage;
import iskallia.vault.world.VaultCrystalMode;
import iskallia.vault.world.VaultLoot;
import iskallia.vault.world.VaultMode;
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
   public static Key<BooleanValue> JOIN_REQUIRE_PARTY;
   public static Key<IntegerValue> TEMPLATE_CACHE_SIZE;
   public static Key<BooleanValue> ALLOW_WAYPOINTS;
   public static Key<BooleanValue> NO_OP_DIFFICULTY;
   public static Key<VaultMode.GameRuleValue> MODE;
   public static Key<VaultLoot.GameRuleValue> LOOT;
   public static Key<VaultCrystalMode.GameRuleValue> CRYSTAL_MODE;

   public static void initialize() {
      FINAL_VAULT_ALLOW_PARTY = register("finalVaultAllowParty", Category.MISC, booleanRule(true));
      JOIN_REQUIRE_PARTY = register("vaultJoinRequireParty", Category.MISC, booleanRule(true));
      TEMPLATE_CACHE_SIZE = register("vaultTemplateCacheSize", Category.MISC, integerRule(32));
      ALLOW_WAYPOINTS = register(
         "vaultAllowWaypoints",
         Category.MISC,
         booleanRule(
            false, (server, value) -> ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundSyncVaultAllowWaypointsMessage(value.get()))
         )
      );
      NO_OP_DIFFICULTY = register("vaultNoOpDifficulty", Category.MISC, booleanRule(true));
      MODE = register("vaultMode", Category.MISC, VaultMode.GameRuleValue.create(VaultMode.NORMAL));
      LOOT = register("vaultLoot", Category.MISC, VaultLoot.GameRuleValue.create(VaultLoot.NORMAL));
      CRYSTAL_MODE = register("vaultCrystalMode", Category.MISC, VaultCrystalMode.GameRuleValue.create(VaultCrystalMode.NORMAL));
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
                     new ClientboundSyncVaultAllowWaypointsMessage(serverPlayer.getLevel().getGameRules().getBoolean(ALLOW_WAYPOINTS))
                  )
            );
      } else {
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.PLAYER.with(() -> player),
               new ClientboundSyncVaultAllowWaypointsMessage(player.getLevel().getGameRules().getBoolean(ALLOW_WAYPOINTS))
            );
      }
   }

   @SubscribeEvent
   public static void syncGameRulesOnDimensionChange(PlayerChangedDimensionEvent event) {
      if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.PLAYER.with(() -> serverPlayer),
               new ClientboundSyncVaultAllowWaypointsMessage(serverPlayer.getLevel().getGameRules().getBoolean(ALLOW_WAYPOINTS))
            );
      }
   }
}
