package iskallia.vault.core.world.generator;

import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.world.storage.IntLatch;
import iskallia.vault.core.world.template.configured.ConfiguredTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class TemplateCache {
   private final Map<RegionPos, ConfiguredTemplate> templates = Collections.synchronizedMap(new HashMap<>());
   private final Map<RegionPos, IntLatch> locks = Collections.synchronizedMap(new HashMap<>());
   private final int size;

   public TemplateCache(int size) {
      this.size = size;
   }

   public ConfiguredTemplate getOrCreate(RegionPos region, Supplier<ConfiguredTemplate> supplier) {
      ConfiguredTemplate template = this.templates.get(region);
      if (template != null) {
         return template;
      } else {
         boolean lockPresent = false;
         IntLatch lock;
         synchronized (this.locks) {
            if (this.locks.containsKey(region)) {
               lockPresent = true;
               lock = this.locks.get(region);
            } else {
               lock = new IntLatch();
               this.locks.put(region, lock);
               lock.increment();
            }
         }

         if (lockPresent) {
            try {
               lock.waitUntil(i -> i == 0);
            } catch (InterruptedException var8) {
               var8.printStackTrace();
            }

            return this.getOrCreate(region, supplier);
         } else {
            template = supplier.get();
            if (template != ConfiguredTemplate.EMPTY) {
               this.templates.put(region, template);
            } else {
               this.locks.remove(region);
            }

            lock.decrement();
            this.clearOldRegions();
            return template;
         }
      }
   }

   private void clearOldRegions() {
      while (this.templates.size() > this.size) {
         Iterator<Entry<RegionPos, ConfiguredTemplate>> it = this.templates.entrySet().iterator();
         this.locks.remove(it.next().getKey());
         it.remove();
      }
   }
}
