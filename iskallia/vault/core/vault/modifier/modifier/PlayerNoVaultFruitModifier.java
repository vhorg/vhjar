package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.time.modifier.FruitExtension;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class PlayerNoVaultFruitModifier extends VaultModifier<PlayerNoVaultFruitModifier.Properties> {
   public PlayerNoVaultFruitModifier(ResourceLocation id, PlayerNoVaultFruitModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.CLOCK_MODIFIER.register(context.getUUID(), data -> {
         if (data.getModifier() instanceof FruitExtension extension) {
            if (!extension.inVault(vault)) {
               return;
            }

            UUID player = data.getModifier().get(FruitExtension.PLAYER);
            if (context.hasTarget() && !player.equals(context.getTarget())) {
               return;
            }

            extension.set(FruitExtension.INCREMENT, Integer.valueOf(0));
            extension.set(FruitExtension.CONSUMED);
         }
      });
   }

   public static class Properties {
   }
}
