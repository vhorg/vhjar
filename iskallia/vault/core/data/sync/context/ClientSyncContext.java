package iskallia.vault.core.data.sync.context;

import iskallia.vault.core.Version;
import java.util.UUID;

public class ClientSyncContext extends SyncContext {
   private final UUID uuid;

   public ClientSyncContext(Version version, UUID uuid) {
      super(version);
      this.uuid = uuid;
   }

   public UUID getUUID() {
      return this.uuid;
   }
}
