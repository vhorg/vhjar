package iskallia.vault.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Event<E extends Event<E, T>, T> {
   protected E parent;
   protected boolean child;
   protected Map<Object, List<Consumer<T>>> listeners;
   protected List<Predicate<T>> conditions;

   protected Event() {
      this.listeners = Collections.synchronizedMap(new LinkedHashMap<>());
      this.child = false;
   }

   protected Event(E parent) {
      this.parent = parent;
      this.conditions = new ArrayList<>();
      this.child = true;
   }

   public Map<Object, List<Consumer<T>>> getListeners() {
      return this.listeners;
   }

   public boolean isChild() {
      return this.child;
   }

   public E getParent() {
      return this.parent;
   }

   public abstract E createChild();

   public T invoke(T data) {
      if (this.isChild()) {
         this.parent.invoke(data);
      } else {
         new ArrayList<>(this.listeners.values()).forEach(list -> list.forEach(consumer -> {
            try {
               consumer.accept(data);
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }));
      }

      return data;
   }

   public E register(Object reference, Consumer<T> listener) {
      if (this.isChild()) {
         this.parent.register(reference, t -> {
            for (Predicate<T> condition : this.conditions) {
               if (!condition.test(t)) {
                  return;
               }
            }

            listener.accept(t);
         });
         return (E)this;
      } else {
         this.listeners.computeIfAbsent(reference, r -> new ArrayList<>()).add(listener);
         return (E)this;
      }
   }

   public E release(Object reference) {
      if (this.isChild()) {
         this.parent.release(reference);
         return (E)this;
      } else {
         this.listeners.remove(reference);
         return (E)this;
      }
   }

   public E filter(Predicate<T> condition) {
      if (this.isChild()) {
         this.conditions.add(condition);
         return (E)this;
      } else {
         return this.createChild().filter(condition);
      }
   }
}
