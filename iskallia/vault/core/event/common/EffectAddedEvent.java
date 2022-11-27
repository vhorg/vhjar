package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;

public class EffectAddedEvent extends ForgeEvent<EffectAddedEvent, PotionAddedEvent> {
   public EffectAddedEvent() {
   }

   protected EffectAddedEvent(EffectAddedEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public EffectAddedEvent createChild() {
      return new EffectAddedEvent(this);
   }
}
