package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;

public class ListenerLeaveEvent extends Event<ListenerLeaveEvent, ListenerLeaveEvent.Data> {
   public ListenerLeaveEvent() {
   }

   protected ListenerLeaveEvent(ListenerLeaveEvent parent) {
      super(parent);
   }

   public ListenerLeaveEvent createChild() {
      return new ListenerLeaveEvent(this);
   }

   public ListenerLeaveEvent.Data invoke(Vault vault, Listener listener) {
      return this.invoke(new ListenerLeaveEvent.Data(vault, listener));
   }

   public static class Data {
      private final Vault vault;
      private final Listener listener;

      public Data(Vault vault, Listener listener) {
         this.vault = vault;
         this.listener = listener;
      }

      public Vault getVault() {
         return this.vault;
      }

      public Listener getListener() {
         return this.listener;
      }
   }
}
