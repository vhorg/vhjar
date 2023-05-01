package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class ZombieReinforcementEvent extends ForgeEvent<ZombieReinforcementEvent, SummonAidEvent> {
   public ZombieReinforcementEvent() {
   }

   protected ZombieReinforcementEvent(ZombieReinforcementEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public ZombieReinforcementEvent createChild() {
      return new ZombieReinforcementEvent(this);
   }
}
