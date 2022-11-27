package iskallia.vault.world.vault.modifier.spi;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;

public interface IVaultModifierBehaviorApply {
   default void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
   }

   default void releaseServer(ModifierContext context) {
      CommonEvents.release(context.getUUID());
   }

   default void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      this.initServer(world, vault, context);
   }

   default void onVaultRemove(VirtualWorld world, Vault vault, ModifierContext context) {
      this.releaseServer(context);
   }

   default void onListenerAdd(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
   }

   default void onListenerRemove(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
   }
}
