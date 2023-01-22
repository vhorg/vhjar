package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.EntityScaler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.player.Player;

public class ClassicMobLogic extends MobLogic {
   public static final SupplierKey<MobLogic> KEY = SupplierKey.of("classic", MobLogic.class).with(Version.v1_0, ClassicMobLogic::new);
   public static final FieldRegistry FIELDS = MobLogic.FIELDS.merge(new FieldRegistry());

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public SupplierKey<MobLogic> getKey() {
      return KEY;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_TICK.register(this, event -> {
         if (event.getEntity().level == world) {
            EntityScaler.scale(vault, event.getEntityLiving());
            if (event.getEntityLiving() instanceof Mob mob) {
               mob.setCanPickUpLoot(false);
               if (mob.getRandom().nextInt(5) == 0) {
                  Player nearestPlayer = world.getNearestPlayer(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), 48.0, false);
                  mob.setTarget(nearestPlayer);
                  mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, nearestPlayer);
                  mob.getBrain().setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, world.getGameTime());
                  mob.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, nearestPlayer);
                  mob.getBrain().setMemory(MemoryModuleType.UNIVERSAL_ANGER, true);
                  mob.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(mob, new ArrayList()) {
                     public boolean contains(LivingEntity p_186108_) {
                        return true;
                     }

                     public boolean contains(Predicate<LivingEntity> p_186131_) {
                        return true;
                     }
                  });
               }
            }
         }
      });
   }
}
