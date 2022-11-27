package iskallia.vault.core.data.sync.context;

import iskallia.vault.core.Version;

public class SyncContext {
   private final Version version;

   public SyncContext(Version version) {
      this.version = version;
   }

   public Version getVersion() {
      return this.version;
   }
}
