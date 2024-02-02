package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;

public class ListenerTickEvent extends Event<ListenerTickEvent, ListenerTickEvent.Data> {
   public ListenerTickEvent() {
   }

   protected ListenerTickEvent(ListenerTickEvent parent) {
      super(parent);
   }

   public ListenerTickEvent createChild() {
      return new ListenerTickEvent(this);
   }

   public ListenerTickEvent.Data invoke(Vault vault, Listener listener, VirtualWorld world) {
      return this.invoke(new ListenerTickEvent.Data(vault, listener, world));
   }

   public static class Data {
      private final Vault vault;
      private final Listener listener;
      private final VirtualWorld world;

      public Data(Vault vault, Listener listener, VirtualWorld world) {
         this.vault = vault;
         this.listener = listener;
         this.world = world;
      }

      public Vault getVault() {
         return this.vault;
      }

      public Listener getListener() {
         return this.listener;
      }

      public VirtualWorld getWorld() {
         return this.world;
      }
   }
}
