package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class PlayerNoExitModifier extends VaultModifier<PlayerNoExitModifier.Properties> {
   public PlayerNoExitModifier(ResourceLocation id, PlayerNoExitModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.ifPresent(Vault.OBJECTIVES, objectives -> objectives.forEach(BailObjective.class, bailObjective -> {
         bailObjective.modify(BailObjective.LOCKED_STACK, i -> i + 1);
         return false;
      }));
   }

   @Override
   public void onVaultRemove(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.ifPresent(Vault.OBJECTIVES, objectives -> objectives.forEach(BailObjective.class, bailObjective -> {
         bailObjective.modify(BailObjective.LOCKED_STACK, i -> i - 1);
         return false;
      }));
   }

   public static class Properties {
   }
}
