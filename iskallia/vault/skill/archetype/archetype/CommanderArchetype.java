package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.CommanderConfig;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CommanderArchetype extends AbstractArchetype<CommanderConfig> {
   private final UUID uuid;

   public CommanderArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.COMMANDER, id);
      this.uuid = UUID.nameUUIDFromBytes(id.toString().getBytes(StandardCharsets.UTF_8));
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

   public void applyToEternal(EternalEntity eternal) {
      AttributeInstance attribute = eternal.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attribute != null && attribute.getModifier(this.uuid) == null) {
         ResourceLocation resourceLocation = this.getRegistryName();
         if (resourceLocation != null) {
            attribute.addPermanentModifier(
               new AttributeModifier(
                  this.uuid, resourceLocation.toString(), this.getConfig().getEternalDamageDealtMultiplier() - 1.0F, Operation.MULTIPLY_TOTAL
               )
            );
         }
      }
   }
}
