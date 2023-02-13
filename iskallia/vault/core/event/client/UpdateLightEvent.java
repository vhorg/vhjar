package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;

public class UpdateLightEvent extends Event<UpdateLightEvent, UpdateLightEvent.Data> {
   public UpdateLightEvent() {
   }

   protected UpdateLightEvent(UpdateLightEvent parent) {
      super(parent);
   }

   public UpdateLightEvent createChild() {
      return new UpdateLightEvent(this);
   }

   public UpdateLightEvent.Data invoke(UpdateLightEvent.Phase phase) {
      return this.invoke(new UpdateLightEvent.Data(phase));
   }

   public UpdateLightEvent phase(UpdateLightEvent.Phase phase) {
      return this.filter(data -> data.phase == phase);
   }

   public record Data(UpdateLightEvent.Phase phase) {
   }

   public static enum Phase {
      PRE,
      POST;
   }
}
