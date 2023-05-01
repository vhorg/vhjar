package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EffectAddedEvent extends ForgeEvent<EffectAddedEvent, PotionAddedEvent> {
   public EffectAddedEvent() {
   }

   protected EffectAddedEvent(EffectAddedEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EffectAddedEvent createChild() {
      return new EffectAddedEvent(this);
   }
}
