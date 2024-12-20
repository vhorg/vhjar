package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.BerserkerConfig;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class BerserkerArchetype extends AbstractArchetype<BerserkerConfig> {
   private final UUID uuid;

   public BerserkerArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.BERSERKER, id);
      this.uuid = UUID.nameUUIDFromBytes(id.toString().getBytes(StandardCharsets.UTF_8));
      MinecraftForge.EVENT_BUS.register(this);
   }

   @Override
   public void onAdded(MinecraftServer server, ServerPlayer player) {
      this.refreshAttributeModifier(player);
   }

   @SubscribeEvent
   public void on(Clone event) {
      if (event.getPlayer() instanceof ServerPlayer player && this.hasThisArchetype(player)) {
         player.getServer().tell(new TickTask(2, () -> this.refreshAttributeModifier(player)));
      }
   }

   private void refreshAttributeModifier(ServerPlayer player) {
      AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
      if (attribute != null) {
         if (attribute.getModifier(this.uuid) == null) {
            ResourceLocation resourceLocation = this.getRegistryName();
            if (resourceLocation != null) {
               attribute.addPermanentModifier(
                  new AttributeModifier(this.uuid, resourceLocation.toString(), this.getConfig().getPlayerMaxLifeMultiplier() - 1.0F, Operation.MULTIPLY_TOTAL)
               );
            }
         }
      }
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
      AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
      if (attribute != null) {
         attribute.removePermanentModifier(this.uuid);
      }
   }
}
