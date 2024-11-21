package iskallia.vault.util;

import com.google.common.collect.ImmutableSet;
import iskallia.vault.entity.entity.EternalEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class EntityReflectTracker {
   private static final Object lock = new Object();
   private static final Map<ResourceKey<Level>, Map<UUID, EntityReflectTracker.ReflectEntry>> entries = new HashMap<>();

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onHurt(LivingHurtEvent event) {
      LivingEntity hurt = event.getEntityLiving();
      if (!(hurt instanceof Player) && !(hurt instanceof EternalEntity) && !hurt.isInvulnerable()) {
         UUID id = hurt.getUUID();
         ResourceKey<Level> dimId = hurt.getLevel().dimension();
         EntityReflectTracker.ReflectEntry entry = new EntityReflectTracker.ReflectEntry(id);
         synchronized (lock) {
            Map<UUID, EntityReflectTracker.ReflectEntry> dimEntries = entries.computeIfAbsent(dimId, k -> new HashMap<>());
            if (dimEntries.containsKey(id)) {
               EntityReflectTracker.ReflectEntry existing = dimEntries.get(id);
               existing.onHit();
               entry = existing;
            } else {
               dimEntries.put(id, entry);
            }
         }

         if (entry.hitCount >= 2 && event.getSource().getEntity() instanceof Player player) {
            float dmgDealt = event.getAmount();
            player.hurt(DamageSource.thorns(hurt), dmgDealt * 0.5F);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      if (entity.tickCount % 10 == 0) {
         if (!(entity instanceof EternalEntity) && entity instanceof Mob mob) {
            UUID id = entity.getUUID();
            ResourceKey dimId = entity.getLevel().dimension();
            EntityReflectTracker.ReflectEntry entry;
            synchronized (lock) {
               entry = entries.getOrDefault(dimId, Collections.emptyMap()).get(id);
            }

            if (entry != null) {
               Entity lastAttacker = entity.getLastHurtByMob();
               if (lastAttacker instanceof Player player && !canCreatePath(mob, lastAttacker.blockPosition())) {
                  float followRange = (float)mob.getAttributeValue(Attributes.FOLLOW_RANGE);
                  int pathTrackingRange = (int)(followRange + 8.0F);
                  if (entity.distanceToSqr(player) < pathTrackingRange * pathTrackingRange) {
                     entity.setLastHurtByMob(player);
                  }

                  entry.resetTimeout();
               } else {
                  entry.tickTimeout = Math.min(entry.tickTimeout, 10);
               }
            }
         }
      }
   }

   private static boolean canCreatePath(Mob mob, BlockPos target) {
      PathNavigation nav = mob.getNavigation();
      float followRange = (float)mob.getAttributeValue(Attributes.FOLLOW_RANGE);
      BlockPos mobPos = mob.blockPosition();
      int range = (int)(followRange + 8.0F);
      PathNavigationRegion region = new PathNavigationRegion(mob.getLevel(), mobPos.offset(-range, -range, -range), mobPos.offset(range, range, range));
      Path path = nav.pathFinder.findPath(region, mob, ImmutableSet.of(target), followRange, 0, 1.0F);
      return path != null && path.canReach();
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase != Phase.START) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv == null) {
            entries.clear();
         } else {
            synchronized (lock) {
               entries.entrySet().removeIf(e -> {
                  Map<UUID, EntityReflectTracker.ReflectEntry> reflectEntries = e.getValue();
                  ServerLevel lvl = srv.getLevel(e.getKey());
                  if (lvl == null) {
                     return true;
                  } else {
                     reflectEntries.forEach((eId, reflectEntry) -> reflectEntry.tickTimeout--);
                     reflectEntries.entrySet().removeIf(entry -> entry.getValue().tickTimeout <= 0);
                     reflectEntries.entrySet().removeIf(entry -> {
                        Entity entity = lvl.getEntity(entry.getKey());
                        return entity == null || !entity.isAlive() || entity.isRemoved();
                     });
                     return reflectEntries.isEmpty();
                  }
               });
            }
         }
      }
   }

   public static class ReflectEntry {
      private final UUID entityId;
      private int hitCount = 0;
      private int tickTimeout;

      public ReflectEntry(UUID entityId) {
         this.entityId = entityId;
         this.resetTimeout();
      }

      public void resetTimeout() {
         this.tickTimeout = 300;
      }

      public void onHit() {
         this.hitCount++;
         this.resetTimeout();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            EntityReflectTracker.ReflectEntry that = (EntityReflectTracker.ReflectEntry)o;
            return Objects.equals(this.entityId, that.entityId);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.entityId);
      }
   }
}
