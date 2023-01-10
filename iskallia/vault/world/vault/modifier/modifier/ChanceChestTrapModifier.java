package iskallia.vault.world.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.spi.AbstractChanceModifier;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class ChanceChestTrapModifier extends AbstractChanceModifier<AbstractChanceModifier.Properties> {
   public ChanceChestTrapModifier(ResourceLocation id, AbstractChanceModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.CHEST_TRAP_GENERATION.register(context.getUUID(), data -> {
         if (data.getPlayer().level == world) {
            if (!context.hasTarget() || context.getTarget().equals(data.getPlayer().getUUID())) {
               data.setProbability(data.getProbability() + this.properties.getChance(context));
            }
         }
      });
   }
}
