package iskallia.vault.util.effect;

import iskallia.vault.util.ServerScheduler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
public final class ScheduledEffectHelper {
   private static final Map<ResourceKey<Level>, List<ScheduledEffectHelper.DelayedEffectEntry>> DELAYED_EFFECT_ENTRIES = new HashMap<>();

   public static void scheduleEffect(LivingEntity entity, MobEffectInstance effectInstance, int delayTicks) {
      int entityId = entity.getId();
      ServerScheduler.INSTANCE.schedule(1, () -> getEntries(entity).add(new ScheduledEffectHelper.DelayedEffectEntry(delayTicks, entityId, effectInstance)));
   }

   public static void invalidateAll(LivingEntity entity) {
      getEntries(entity).stream().filter(entry -> entry.matches(entity.getId())).forEach(ScheduledEffectHelper.DelayedEffectEntry::invalidate);
   }

   public static void invalidateAll(LivingEntity entity, MobEffect effect) {
      getEntries(entity)
         .stream()
         .filter(entry -> entry.matches(entity.getId()) && entry.matches(effect))
         .forEach(ScheduledEffectHelper.DelayedEffectEntry::invalidate);
   }

   public static void invalidateAll(Level level, MobEffect effect) {
      getEntries(level).stream().filter(entry -> entry.matches(effect)).forEach(ScheduledEffectHelper.DelayedEffectEntry::invalidate);
   }

   private static List<ScheduledEffectHelper.DelayedEffectEntry> getEntries(Entity entity) {
      return getEntries(entity.getLevel().dimension());
   }

   private static List<ScheduledEffectHelper.DelayedEffectEntry> getEntries(Level level) {
      return getEntries(level.dimension());
   }

   private static List<ScheduledEffectHelper.DelayedEffectEntry> getEntries(ResourceKey<Level> key) {
      return DELAYED_EFFECT_ENTRIES.computeIfAbsent(key, levelResourceKey -> new ArrayList<>());
   }

   @SubscribeEvent
   public static void on(WorldTickEvent event) {
      if (event.phase == Phase.START && !event.world.isClientSide()) {
         List<ScheduledEffectHelper.DelayedEffectEntry> entries = getEntries(event.world);

         for (ScheduledEffectHelper.DelayedEffectEntry entry : entries) {
            if (entry.valid && entry.remainingTicks <= 0) {
               entry.invalidate();
               Entity entity = event.world.getEntity(entry.entityId);
               if (entity instanceof LivingEntity) {
                  LivingEntity livingEntity = (LivingEntity)entity;
                  if (entity.isAlive()) {
                     livingEntity.addEffect(entry.effectInstance);
                  }
               }
            }
         }

         entries.forEach(ScheduledEffectHelper.DelayedEffectEntry::decrement);
         entries.removeIf(entryx -> !entryx.valid);
      }
   }

   private ScheduledEffectHelper() {
   }

   private static class DelayedEffectEntry {
      private int remainingTicks;
      private final int entityId;
      private final MobEffectInstance effectInstance;
      private boolean valid = true;

      private DelayedEffectEntry(int remainingTicks, int entityId, MobEffectInstance effectInstance) {
         this.remainingTicks = remainingTicks;
         this.entityId = entityId;
         this.effectInstance = effectInstance;
      }

      private boolean matches(MobEffect effect) {
         return this.effectInstance.getEffect() == effect;
      }

      private boolean matches(int entityId) {
         return this.entityId == entityId;
      }

      private void decrement() {
         this.remainingTicks--;
      }

      private void invalidate() {
         this.valid = false;
      }
   }
}
