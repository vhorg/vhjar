package iskallia.vault.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Targeting {
   private static final Set<Targeting.TargetingOverride> targetingOverrides = new HashSet<>();

   public static void addIgnoredTargetOverride(BiPredicate<LivingEntity, Entity> targetMatcher) {
      targetingOverrides.add((attacker, target) -> targetMatcher.test(attacker, target) ? Targeting.TargetingResult.IGNORE : Targeting.TargetingResult.DEFAULT);
   }

   public static void addForcedTargetOverride(BiPredicate<LivingEntity, Entity> targetMatcher) {
      targetingOverrides.add((attacker, target) -> targetMatcher.test(attacker, target) ? Targeting.TargetingResult.TARGET : Targeting.TargetingResult.DEFAULT);
   }

   public static Targeting.TargetingResult getTargetingResult(LivingEntity attacker, Entity target) {
      for (Targeting.TargetingOverride targetingOverride : targetingOverrides) {
         Targeting.TargetingResult result = targetingOverride.getResult(attacker, target);
         if (result != Targeting.TargetingResult.DEFAULT) {
            return result;
         }
      }

      return Targeting.TargetingResult.DEFAULT;
   }

   @SubscribeEvent
   public static void onTarget(LivingSetAttackTargetEvent event) {
      LivingEntity attacker = event.getEntityLiving();
      if (getTargetingResult(attacker, event.getTarget()) == Targeting.TargetingResult.IGNORE && attacker instanceof Mob mob) {
         mob.setTarget(null);
         attacker.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         attacker.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      } else {
         attacker.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, event.getTarget());
         attacker.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      }
   }

   static {
      targetingOverrides.add(
         (attacker, target) -> !(target instanceof Player player && (player.isCreative() || player.isSpectator()))
            ? Targeting.TargetingResult.DEFAULT
            : Targeting.TargetingResult.IGNORE
      );
   }

   public interface TargetingOverride {
      Targeting.TargetingResult getResult(LivingEntity var1, Entity var2);
   }

   public static enum TargetingResult {
      TARGET(true),
      IGNORE(false),
      DEFAULT(null);

      private Boolean shouldTarget;

      private TargetingResult(Boolean shouldTarget) {
         this.shouldTarget = shouldTarget;
      }

      public Boolean getShouldTarget() {
         return this.shouldTarget;
      }
   }
}
