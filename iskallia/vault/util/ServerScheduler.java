package iskallia.vault.util;

import java.util.Iterator;
import java.util.LinkedList;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;

public class ServerScheduler {
   public static final ServerScheduler INSTANCE = new ServerScheduler();
   private static final Object lock = new Object();
   private boolean inTick = false;
   private final LinkedList<Tuple<Runnable, Counter>> queue = new LinkedList<>();
   private final LinkedList<Tuple<Runnable, Integer>> waiting = new LinkedList<>();

   private ServerScheduler() {
   }

   public void onServerTick(ServerTickEvent event) {
      if (event.phase != Phase.START) {
         this.inTick = true;
         synchronized (lock) {
            this.inTick = true;
            Iterator<Tuple<Runnable, Counter>> iterator = this.queue.iterator();

            while (iterator.hasNext()) {
               Tuple<Runnable, Counter> r = iterator.next();
               ((Counter)r.func_76340_b()).decrement();
               if (((Counter)r.func_76340_b()).getValue() <= 0) {
                  ((Runnable)r.func_76341_a()).run();
                  iterator.remove();
               }
            }

            this.inTick = false;

            for (Tuple<Runnable, Integer> wait : this.waiting) {
               this.queue.addLast(new Tuple(wait.func_76341_a(), new Counter((Integer)wait.func_76340_b())));
            }
         }

         this.waiting.clear();
      }
   }

   public void schedule(int tickDelay, Runnable r) {
      synchronized (lock) {
         if (this.inTick) {
            this.waiting.addLast(new Tuple(r, tickDelay));
         } else {
            this.queue.addLast(new Tuple(r, new Counter(tickDelay)));
         }
      }
   }
}
