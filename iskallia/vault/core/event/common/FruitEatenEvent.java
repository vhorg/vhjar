package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.world.entity.player.Player;

public class FruitEatenEvent extends Event<FruitEatenEvent, FruitEatenEvent.Data> {
   public FruitEatenEvent() {
   }

   protected FruitEatenEvent(FruitEatenEvent parent) {
      super(parent);
   }

   public FruitEatenEvent createChild() {
      return new FruitEatenEvent(this);
   }

   public FruitEatenEvent.Data invoke(ItemVaultFruit fruit, Player player, int time) {
      return this.invoke(new FruitEatenEvent.Data(fruit, player, time));
   }

   public static class Data {
      private final ItemVaultFruit fruit;
      private final Player player;
      private int time;

      public Data(ItemVaultFruit fruit, Player player, int time) {
         this.fruit = fruit;
         this.player = player;
         this.time = time;
      }

      public ItemVaultFruit getFruit() {
         return this.fruit;
      }

      public Player getPlayer() {
         return this.player;
      }

      public int getTime() {
         return this.time;
      }

      public void setTime(int time) {
         this.time = time;
      }
   }
}
