package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.modifier.ClockModifier;

public class ClockModifierEvent extends Event<ClockModifierEvent, ClockModifierEvent.Data> {
   public ClockModifierEvent() {
   }

   protected ClockModifierEvent(ClockModifierEvent parent) {
      super(parent);
   }

   public ClockModifierEvent createChild() {
      return new ClockModifierEvent(this);
   }

   public ClockModifierEvent.Data invoke(TickClock clock, ClockModifier modifier) {
      return this.invoke(new ClockModifierEvent.Data(clock, modifier));
   }

   public static class Data {
      private final TickClock clock;
      private final ClockModifier modifier;

      public Data(TickClock clock, ClockModifier modifier) {
         this.clock = clock;
         this.modifier = modifier;
      }

      public TickClock getClock() {
         return this.clock;
      }

      public ClockModifier getModifier() {
         return this.modifier;
      }
   }
}
