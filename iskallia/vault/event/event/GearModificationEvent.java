package iskallia.vault.event.event;

import iskallia.vault.gear.modification.GearModification;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class GearModificationEvent extends PlayerEvent {
   private final GearModification modification;

   public GearModificationEvent(Player player, GearModification modification) {
      super(player);
      this.modification = modification;
   }

   public GearModification getModification() {
      return this.modification;
   }
}
