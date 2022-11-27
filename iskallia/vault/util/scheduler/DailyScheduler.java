package iskallia.vault.util.scheduler;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModTasks;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DailyScheduler {
   private static DailyScheduler scheduler;
   private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

   private DailyScheduler() {
   }

   public static void start(ServerStartingEvent event) {
      scheduler = new DailyScheduler();
      ModTasks.initTasks(scheduler, event.getServer());
   }

   public void scheduleServer(int hour, Runnable task) {
      this.scheduleServer(hour, 0, 0, task);
   }

   public void scheduleServer(int hour, int minute, int second, Runnable task) {
      if (scheduler == null) {
         throw new IllegalStateException("Startup not finished, Scheduler not running!");
      } else {
         ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
         ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(second);
         if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1L);
         }

         scheduler.executorService.scheduleAtFixedRate(() -> {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            srv.execute(task);
         }, Duration.between(now, nextRun).getSeconds(), TimeUnit.DAYS.toSeconds(1L), TimeUnit.SECONDS);
      }
   }

   public static void stop(ServerStoppingEvent event) {
      scheduler.executorService.shutdown();

      try {
         scheduler.executorService.awaitTermination(1L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         VaultMod.LOGGER.error(var2);
      }

      scheduler = null;
   }
}
