package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class PlayerAttributeModifier extends EntityAttributeModifier<EntityAttributeModifier.Properties> {
   public PlayerAttributeModifier(ResourceLocation id, EntityAttributeModifier.Properties properties, VaultModifier.Display display) {
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
                     synchronized (event.player) {
                        this.applyToEntity(event.player, context.getUUID(), context);
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
            this.removeFromEntity(event.player);
         }
      }
   }
}
