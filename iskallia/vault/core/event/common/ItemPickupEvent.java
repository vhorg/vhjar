package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import java.util.function.Consumer;
import net.minecraftforge.common.MinecraftForge;

public class ItemPickupEvent extends ForgeEvent<ItemPickupEvent, net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent> {
   public ItemPickupEvent() {
   }

   protected ItemPickupEvent(ItemPickupEvent parent) {
      super(parent);
   }

   public ItemPickupEvent createChild() {
      return new ItemPickupEvent(this);
   }

   @Override
   protected void register() {
      Consumer<net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent> listener = this::invoke;
      MinecraftForge.EVENT_BUS.addListener(listener);
   }
}
