package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class PlayerHealCutModifier extends EntityAttributeModifier<EntityAttributeModifier.Properties> {
   public PlayerHealCutModifier(ResourceLocation id, EntityAttributeModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      MinecraftForge.EVENT_BUS.register(this);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLAYER_TICK
         .at(Phase.START)
         .register(
            context.getUUID(),
            event -> {
               if (vault.get(Vault.LISTENERS).contains(event.player.getUUID())
                  && event.player.getServer() != null
                  && event.player.getLevel().getGameTime() % 10L == 0L) {
                  if (!context.hasTarget() || context.getTarget().equals(event.player.getUUID())) {
                     UUID uuid = this.getId(context.getUUID());
                     AttributeInstance attribute = event.player.getAttribute(Attributes.MAX_HEALTH);
                     if (attribute != null) {
                        synchronized (event.player) {
                           attribute.removeModifier(uuid);
                           attribute.addTransientModifier(
                              new AttributeModifier(uuid, "Heal Cut", event.player.getHealth() - event.player.getMaxHealth(), Operation.ADDITION)
                           );
                        }
                     }
                  }
               }
            }
         );
   }

   @SubscribeEvent
   public void on(PlayerTickEvent event) {
      if (event.side != LogicalSide.CLIENT && event.player.getLevel().getGameTime() % 10L == 0L && !ServerVaults.get(event.player.level).isPresent()) {
         synchronized (event.player) {
            AttributeInstance attribute = event.player.getAttribute(Attributes.MAX_HEALTH);
            if (attribute != null) {
               for (AttributeModifier modifier : new HashSet(attribute.getModifiers())) {
                  if (this.isId(modifier.getId())) {
                     attribute.removeModifier(modifier);
                  }
               }
            }
         }
      }
   }
}
