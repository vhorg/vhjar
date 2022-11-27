package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.WardConfig;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class WardArchetype extends AbstractArchetype<WardConfig> {
   private final UUID uuid;

   public WardArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.WARD, id);
      this.uuid = UUID.nameUUIDFromBytes(id.toString().getBytes(StandardCharsets.UTF_8));
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.BLOCK_CHANCE)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(this, data -> data.setValue(data.getValue() + this.getConfig().getAdditionalBlockChanceWithShieldEquipped()));
   }

   @Override
   public void onTick(MinecraftServer server, ServerPlayer player) {
      if (!PlayerDamageHelper.hasMultiplier(player, this.uuid)) {
         PlayerDamageHelper.applyMultiplier(
            this.uuid, player, this.getConfig().getPlayerDamageDealtMultiplier(), PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY
         );
      }
   }

   @Override
   public void onRemoved(MinecraftServer server, ServerPlayer player) {
      PlayerDamageHelper.removeMultiplier(player, this.uuid);
   }
}
