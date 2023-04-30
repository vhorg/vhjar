package iskallia.vault.event.event;

import iskallia.vault.bounty.task.Task;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class BountyCompleteEvent extends Event {
   private final Player player;
   private final Task<?> task;

   public BountyCompleteEvent(Player player, Task<?> task) {
      this.player = player;
      this.task = task;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Task<?> getTask() {
      return this.task;
   }
}
