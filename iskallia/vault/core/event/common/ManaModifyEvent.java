package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.mana.ManaPlayer;

public class ManaModifyEvent extends Event<ManaModifyEvent, ManaModifyEvent.Data> {
   public ManaModifyEvent() {
   }

   protected ManaModifyEvent(ManaModifyEvent parent) {
      super(parent);
   }

   public ManaModifyEvent createChild() {
      return new ManaModifyEvent(this);
   }

   public ManaModifyEvent.Data invoke(ManaPlayer player, float oldAmount, float newAmount) {
      return this.invoke(new ManaModifyEvent.Data(player, oldAmount, newAmount));
   }

   public static class Data {
      private final ManaPlayer player;
      private final float oldAmount;
      private final float newAmount;

      public Data(ManaPlayer player, float oldAmount, float newAmount) {
         this.player = player;
         this.oldAmount = oldAmount;
         this.newAmount = newAmount;
      }

      public ManaPlayer getPlayer() {
         return this.player;
      }

      public float getOldAmount() {
         return this.oldAmount;
      }

      public float getNewAmount() {
         return this.newAmount;
      }
   }
}
