package iskallia.vault.core.world.storage;

import java.util.Optional;

public interface IZonedWorld {
   boolean isBypassed();

   void setBypassed(boolean var1);

   WorldZones getZones();

   default void runWithBypass(boolean bypassed, Runnable runnable) {
      boolean value = this.isBypassed();
      this.setBypassed(bypassed);
      runnable.run();
      this.setBypassed(value);
   }

   static Optional<IZonedWorld> of(Object object) {
      return object instanceof IZonedWorld proxy ? Optional.of(proxy) : Optional.empty();
   }

   static void runWithBypass(Object object, boolean bypassed, Runnable runnable) {
      of(object).ifPresent(proxy -> proxy.runWithBypass(bypassed, runnable));
   }
}
