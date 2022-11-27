package iskallia.vault.core.data.sync.handler;

import iskallia.vault.core.data.sync.context.ClientSyncContext;
import iskallia.vault.core.data.sync.context.SyncContext;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;

public class ClientSyncHandler<T> implements SyncHandler<T> {
   private final Predicate<UUID> uuidFilter;

   protected ClientSyncHandler(Predicate<UUID> uuidFilter) {
      this.uuidFilter = uuidFilter;
   }

   @Override
   public boolean canSync(T value, SyncContext context) {
      return context instanceof ClientSyncContext clientContext ? this.uuidFilter.test(clientContext.getUUID()) : false;
   }

   public static class Factory {
      public <T> ClientSyncHandler<T> all() {
         return new ClientSyncHandler<>(uuid -> true);
      }

      public <T> ClientSyncHandler<T> of(UUID uuid) {
         return new ClientSyncHandler<>(other -> other.equals(uuid));
      }

      public <T> ClientSyncHandler<T> of(Player player) {
         return this.of(player.getUUID());
      }
   }
}
