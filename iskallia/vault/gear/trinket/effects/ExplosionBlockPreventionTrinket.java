package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ExplosionBlockPreventionTrinket extends TrinketEffect.Simple {
   public static final float CHECK_SIZE = 16.0F;

   public ExplosionBlockPreventionTrinket(ResourceLocation name) {
      super(name);
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
}
