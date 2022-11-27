package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.entity.player.Player;

public class PlayerRegenEvent extends Event<PlayerRegenEvent, PlayerRegenEvent.Data> {
   public PlayerRegenEvent() {
   }

   protected PlayerRegenEvent(PlayerRegenEvent parent) {
      super(parent);
   }

   public PlayerRegenEvent createChild() {
      return new PlayerRegenEvent(this);
   }

   public PlayerRegenEvent.Data invoke(Player player, float amount) {
      return this.invoke(new PlayerRegenEvent.Data(player, amount));
   }

   public static class Data {
      private final Player player;
      private float amount;

      public Data(Player player, float amount) {
         this.player = player;
         this.amount = amount;
      }

      public Player getPlayer() {
         return this.player;
      }

      public float getAmount() {
         return this.amount;
      }

      public void setAmount(float amount) {
         this.amount = amount;
      }
   }
}
