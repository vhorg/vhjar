package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.AbstractChanceModifier;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class ChanceArtifactModifier extends AbstractChanceModifier<AbstractChanceModifier.Properties> {
   public ChanceArtifactModifier(ResourceLocation id, AbstractChanceModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ARTIFACT_CHANCE.register(context.getUUID(), data -> data.getListener().getPlayer().ifPresent(player -> {
         if (player.level == world) {
            if (!context.hasTarget() || context.getTarget().equals(data.getListener().getId())) {
               data.setProbability(data.getProbability() + this.properties.getChance(context));
            }
         }
      }));
   }
}
