package iskallia.vault.util.damage;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.util.ServerScheduler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class DamageOverTimeHelper {
   private static final Map<ResourceKey<Level>, List<DamageOverTimeHelper.DamageOverTimeEntry>> worldEntries = new HashMap<>();

   public static void applyDamageOverTime(LivingEntity target, DamageSource damageSource, float totalDamage, int durationTicks) {
      ServerScheduler.INSTANCE
         .schedule(
            1,
            () -> {
               DamageOverTimeHelper.DamageOverTimeEntry entry = new DamageOverTimeHelper.DamageOverTimeEntry(
                  durationTicks, damageSource, target.getId(), totalDamage / (durationTicks / 20.0F)
               );
               worldEntries.computeIfAbsent(target.getCommandSenderWorld().dimension(), key -> new ArrayList<>()).add(entry);
            }
         );
   }

   public static void invalidateAll(LivingEntity target) {
      getDotEntries(target).forEach(DamageOverTimeHelper.DamageOverTimeEntry::invalidate);
   }

   public static List<DamageOverTimeHelper.DamageOverTimeEntry> getDotEntries(Entity entity) {
      Level entityWorld = entity.getCommandSenderWorld();
      List<DamageOverTimeHelper.DamageOverTimeEntry> allEntries = worldEntries.get(entityWorld.dimension());
      List<DamageOverTimeHelper.DamageOverTimeEntry> entries = new LinkedList<>();
      if (allEntries == null) {
         return entries;
      } else {
         for (DamageOverTimeHelper.DamageOverTimeEntry entry : allEntries) {
            if (entry.entityId == entity.getId()) {
               entries.add(entry);
            }
         }

         return entries;
      }
   }

   @SubscribeEvent
   public static void onWorldTick(WorldTickEvent event) {
      if (event.phase != Phase.END) {
         Level world = event.world;
         if (!world.isClientSide()) {
            List<DamageOverTimeHelper.DamageOverTimeEntry> entries = worldEntries.computeIfAbsent(world.dimension(), key -> new ArrayList<>());
            ActiveFlags.IS_DOT_ATTACKING.runIfNotSet(() -> entries.forEach(entry -> {
               if (entry.valid && entry.ticks % 20 == 0) {
                  Entity e = world.getEntity(entry.entityId);
                  if (e instanceof LivingEntity && e.isAlive()) {
                     DamageUtil.shotgunAttack(e, entity -> entity.hurt(entry.source, entry.damagePerSecond));
                  } else {
                     entry.invalidate();
                  }
               }
            }));
            entries.forEach(DamageOverTimeHelper.DamageOverTimeEntry::decrement);
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
