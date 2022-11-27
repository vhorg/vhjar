package iskallia.vault.client.gui.framework.spatial;

import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.framework.spatial.spi.ILayoutDebugLogger;
import org.apache.logging.log4j.Logger;

public final class LayoutDebugLoggers {
   public static final ILayoutDebugLogger NONE = new ILayoutDebugLogger() {
      @Override
      public void out(String message) {
      }

      @Override
      public void out(String message, Object p0) {
      }

      @Override
      public void out(String message, Object p0, Object p1) {
      }

      @Override
      public void out(String message, Object p0, Object p1, Object p2) {
      }
   };

   public static ILayoutDebugLogger getModLogger() {
      return getLogger(VaultMod.LOGGER);
   }

   public static ILayoutDebugLogger getLogger(final Logger logger) {
      return new ILayoutDebugLogger() {
         @Override
         public void out(String message) {
            logger.debug(message);
         }

         @Override
         public void out(String message, Object p0) {
            logger.debug(message, p0);
         }

         @Override
         public void out(String message, Object p0, Object p1) {
            logger.debug(message, p0, p1);
         }

         @Override
         public void out(String message, Object p0, Object p1, Object p2) {
            logger.debug(message, p0, p1, p2);
         }
      };
   }

   private LayoutDebugLoggers() {
   }
}
