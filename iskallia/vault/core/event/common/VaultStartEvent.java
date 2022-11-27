package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;

public class VaultStartEvent extends Event<VaultStartEvent, VaultStartEvent.Data> {
   public VaultStartEvent() {
   }

   protected VaultStartEvent(VaultStartEvent parent) {
      super(parent);
   }

   public VaultStartEvent createChild() {
      return new VaultStartEvent(this);
   }

   public VaultStartEvent.Data invoke(Vault vault) {
      return this.invoke(new VaultStartEvent.Data(vault));
   }

   public static class Data {
      private final Vault vault;

      public Data(Vault vault) {
         this.vault = vault;
      }

      public Vault getVault() {
         return this.vault;
      }
   }
}
