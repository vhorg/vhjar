package iskallia.vault.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.eventbus.api.EventPriority;

public abstract class ForgeEvent<E extends ForgeEvent<E, T>, T extends net.minecraftforge.eventbus.api.Event> extends Event<E, T> {
   protected ForgeEvent() {
      this.register();
   }

   protected ForgeEvent(E parent) {
      super(parent);
   }

   protected abstract void register();

   public E register(Object reference, EventPriority eventPriority, Consumer<T> listener) {
      return this.register(reference, eventPriority, false, listener, 0);
   }

   public E register(Object reference, EventPriority eventPriority, boolean receiveCancelled, Consumer<T> listener) {
      return this.createChild().register(reference, eventPriority, receiveCancelled, listener, 0);
   }

   public E register(Object reference, Consumer<T> listener, int priority) {
      return this.register(reference, EventPriority.NORMAL, false, listener, priority);
   }

   public E register(Object reference, EventPriority eventPriority, Consumer<T> listener, int priority) {
      return this.register(reference, eventPriority, false, listener, priority);
   }

   public E register(Object reference, EventPriority eventPriority, boolean receiveCancelled, Consumer<T> listener, int priority) {
      if (this.isChild()) {
         this.parent.listeners.computeIfAbsent(priority, p -> Collections.synchronizedMap(new LinkedHashMap<>()));
         this.parent.listeners.get(priority).computeIfAbsent(reference, r -> new ArrayList<>()).add(e -> {
            if (e.getPhase() == eventPriority && (receiveCancelled || !e.isCanceled())) {
               for (Predicate<T> condition : this.conditions) {
                  if (!condition.test(e)) {
                     return;
                  }
               }

               listener.accept(e);
            }
         });
         return (E)this;
      } else {
         return this.createChild().register(reference, eventPriority, receiveCancelled, listener, priority);
      }
   }
}
