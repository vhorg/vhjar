package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;

public class VaultEndEvent extends Event<VaultEndEvent, VaultEndEvent.Data> {
   public VaultEndEvent() {
   }

   protected VaultEndEvent(VaultEndEvent parent) {
      super(parent);
   }

   public VaultEndEvent createChild() {
      return new VaultEndEvent(this);
   }

   public VaultEndEvent.Data invoke(Vault vault) {
      return this.invoke(new VaultEndEvent.Data(vault));
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
