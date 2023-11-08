package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;
import net.minecraft.client.renderer.DimensionSpecialEffects;

public class DimensionEffectEvent extends Event<DimensionEffectEvent, DimensionEffectEvent.Data> {
   public DimensionEffectEvent() {
   }

   protected DimensionEffectEvent(DimensionEffectEvent parent) {
      super(parent);
   }

   public DimensionEffectEvent createChild() {
      return new DimensionEffectEvent(this);
   }

   public DimensionEffectEvent.Data invoke(DimensionSpecialEffects effects) {
      return this.invoke(new DimensionEffectEvent.Data(effects));
   }

   public static class Data {
      private DimensionSpecialEffects effects;

      public Data(DimensionSpecialEffects effects) {
         this.effects = effects;
      }

      public DimensionSpecialEffects getEffects() {
         return this.effects;
      }

      public void setEffects(DimensionSpecialEffects effects) {
         this.effects = effects;
      }
   }
}
