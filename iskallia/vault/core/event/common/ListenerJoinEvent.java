package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;

public class ListenerJoinEvent extends Event<ListenerJoinEvent, ListenerJoinEvent.Data> {
   public ListenerJoinEvent() {
   }

   protected ListenerJoinEvent(ListenerJoinEvent parent) {
      super(parent);
   }

   public ListenerJoinEvent createChild() {
      return new ListenerJoinEvent(this);
   }

   public ListenerJoinEvent.Data invoke(Vault vault, Listener listener) {
      return this.invoke(new ListenerJoinEvent.Data(vault, listener));
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
