package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.FarmerAnimalConfig;
import iskallia.vault.skill.ability.effect.FarmerAbility;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.phys.AABB;

public class FarmerAnimalAbility extends FarmerAbility<FarmerAnimalConfig> {
   private static final int PARTICLE_COUNT = 20;
   private static final Predicate<AgeableMob> AGEABLE_MOB_PREDICATE = entity -> entity.isAlive() && !entity.isSpectator() && entity.isBaby();

   protected void doGrow(FarmerAnimalConfig config, ServerPlayer player, ServerLevel world) {
      super.doGrow(config, player, world);
      AABB searchBox = player.getBoundingBox().inflate(config.getHorizontalRange(), config.getVerticalRange(), config.getHorizontalRange());

      for (AgeableMob entity : world.getEntitiesOfClass(AgeableMob.class, searchBox, AGEABLE_MOB_PREDICATE)) {
         if (RANDOM.nextFloat() < 0.4F) {
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER, entity.getX(), entity.getY(), entity.getZ(), 20, 0.5, 0.5, 0.5, 0.0);
         }

         if (RANDOM.nextFloat() < config.getAdultChance()) {
            entity.setBaby(false);
         }
      }
   }
}
