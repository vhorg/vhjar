package iskallia.vault.gear.trinket.effects;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ExplosionBlockPreventionTrinket extends TrinketEffect<ExplosionBlockPreventionTrinket.Config> {
   public static final float CHECK_SIZE = 16.0F;
   float chance;

   public ExplosionBlockPreventionTrinket(ResourceLocation name, float chance) {
      super(name);
      this.chance = chance;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onExplosion(Detonate event) {
      Explosion explosion = event.getExplosion();
      if (!explosion.getToBlow().isEmpty()) {
         AABB gatherBox = AABB.ofSize(explosion.getPosition(), 16.0, 16.0, 16.0);

         for (Player player : event.getWorld().getNearbyPlayers(TargetingConditions.forNonCombat(), null, gatherBox)) {
            for (TrinketHelper.TrinketStack<ExplosionBlockPreventionTrinket> trinketStack : TrinketHelper.getTrinkets(
               player, ExplosionBlockPreventionTrinket.class
            )) {
               if (trinketStack.isUsable(player)) {
                  explosion.clearToBlow();
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingAttackEvent event) {
      if (event.getEntityLiving() instanceof Player player) {
         for (TrinketHelper.TrinketStack<ExplosionBlockPreventionTrinket> trinketStack : TrinketHelper.getTrinkets(
            player, ExplosionBlockPreventionTrinket.class
         )) {
            if (trinketStack.isUsable(player)) {
               Entity hitBy = event.getSource().getDirectEntity();
               if (hitBy instanceof Arrow) {
                  Arrow projectile = (Arrow)hitBy;
                  if (player.getRandom().nextFloat() <= trinketStack.trinket().getConfig().getChance()) {
                     player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.CAT_HURT, SoundSource.BLOCKS, 0.2F, 1.0F);
                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   @Override
   public Class<ExplosionBlockPreventionTrinket.Config> getConfigClass() {
      return ExplosionBlockPreventionTrinket.Config.class;
   }

   public ExplosionBlockPreventionTrinket.Config getDefaultConfig() {
      return new ExplosionBlockPreventionTrinket.Config(this.chance);
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private float chance;

      public Config(float chance) {
         this.chance = chance;
      }

      public float getChance() {
         return this.chance;
      }
   }
}
