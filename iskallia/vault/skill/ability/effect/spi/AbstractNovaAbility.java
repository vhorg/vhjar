package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.NovaConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractNovaAbility<C extends NovaConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Nova";
   }

   @NotNull
   protected List<LivingEntity> getTargetEntities(C config, ServerPlayer player) {
      float radius = config.getRadius(player);
      return player.level
         .getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.forCombat().range(radius).selector(entity -> !(entity instanceof Player)),
            player,
            player.getBoundingBox().inflate(radius * 2.0F)
         );
   }

   protected float getAttackDamage(C config, ServerPlayer player) {
      return (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE) * config.getPercentAttackDamageDealt();
   }

   protected void doParticles(C config, ServerPlayer player) {
      float radius = config.getRadius(player);
      int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 50.0, 50.0, 200.0);
      ((ServerLevel)player.level)
         .sendParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), particleCount, radius * 0.5, 0.5, radius * 0.5, 0.0);
   }
}
