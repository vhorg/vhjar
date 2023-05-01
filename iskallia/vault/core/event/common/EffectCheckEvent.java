package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EffectCheckEvent extends ForgeEvent<EffectCheckEvent, PotionApplicableEvent> {
   public EffectCheckEvent() {
   }

   protected EffectCheckEvent(EffectCheckEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EffectCheckEvent createChild() {
      return new EffectCheckEvent(this);
   }
}
