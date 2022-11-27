package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.EntityScaler;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
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
                  if (mob instanceof Piglin) {
                     Brain<?> brain = mob.getBrain();
                     brain.setMemory(MemoryModuleType.ATTACK_TARGET, nearestPlayer);
                     brain.setMemory(MemoryModuleType.UNIVERSAL_ANGER, true);
                  }
               }
            }
         }
      });
   }
}
