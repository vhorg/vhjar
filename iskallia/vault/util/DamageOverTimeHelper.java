package iskallia.vault.util;

import iskallia.vault.event.ActiveFlags;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class DamageOverTimeHelper {
   private static final Map<RegistryKey<World>, List<DamageOverTimeHelper.DamageOverTimeEntry>> worldEntries = new HashMap<>();

   public static void applyDamageOverTime(LivingEntity target, DamageSource damageSource, float totalDamage, int seconds) {
      ServerScheduler.INSTANCE
         .schedule(
            1,
            () -> {
               DamageOverTimeHelper.DamageOverTimeEntry entry = new DamageOverTimeHelper.DamageOverTimeEntry(
                  seconds * 20, damageSource, target.func_145782_y(), totalDamage / seconds
               );
               worldEntries.computeIfAbsent(target.func_130014_f_().func_234923_W_(), key -> new ArrayList<>()).add(entry);
            }
         );
   }

   @SubscribeEvent
   public static void onWorldTick(WorldTickEvent event) {
      if (event.phase != Phase.END) {
         World world = event.world;
         if (!world.func_201670_d()) {
            List<DamageOverTimeHelper.DamageOverTimeEntry> entries = worldEntries.computeIfAbsent(world.func_234923_W_(), key -> new ArrayList<>());
            entries.forEach(rec$ -> rec$.decrement());
            ActiveFlags.IS_DOT_ATTACKING.runIfNotSet(() -> entries.forEach(entry -> {
               if (entry.ticks % 20 == 0) {
                  Entity e = world.func_73045_a(entry.entityId);
                  if (e instanceof LivingEntity && e.func_70089_S()) {
                     DamageUtil.shotgunAttack(e, entity -> entity.func_70097_a(entry.source, entry.damagePerSecond));
                  } else {
                     entry.invalidate();
                  }
               }
            }));
            entries.removeIf(entry -> !entry.valid);
         }
      }
   }

   private static class DamageOverTimeEntry {
      private int ticks;
      private final DamageSource source;
      private final int entityId;
      private final float damagePerSecond;
      private boolean valid = true;

      public DamageOverTimeEntry(int ticks, DamageSource source, int entityId, float damagePerSecond) {
         this.ticks = ticks;
         this.source = source;
         this.entityId = entityId;
         this.damagePerSecond = damagePerSecond;
      }

      private void decrement() {
         this.ticks--;
         this.valid = this.valid && this.ticks > 0;
      }

      private void invalidate() {
         this.valid = false;
      }
   }
}
