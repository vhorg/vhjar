package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketHelper;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class WallClimbingTrinket extends TrinketEffect.Simple {
   public WallClimbingTrinket(ResourceLocation name) {
      super(name);
   }

   @SubscribeEvent
   public static void onTick(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof Player player) {
         List<TrinketHelper.TrinketStack<WallClimbingTrinket>> trinkets = TrinketHelper.getTrinkets(player, WallClimbingTrinket.class);
         if (!trinkets.stream().noneMatch(trinketStack -> trinketStack.isUsable(player))) {
            if (player.horizontalCollision && !player.isInWater()) {
               player.fallDistance = 0.0F;
               Vec3 motion = player.getDeltaMovement();
               float yAccel = player.getSpeed() * 1.4F;
               if (player.isShiftKeyDown() || !player.getFeetBlockState().isScaffolding(player) && player.isSuppressingSlidingDownLadder()) {
                  yAccel = 0.0F;
               }

               motion = new Vec3(Mth.clamp(motion.x, -0.15F, 0.15F), yAccel, Mth.clamp(motion.z, -0.15F, 0.15F));
               player.setDeltaMovement(motion);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onFlyIntoWall(LivingAttackEvent event) {
      if (event.getSource() == DamageSource.FLY_INTO_WALL) {
         if (event.getEntityLiving() instanceof Player player) {
            TrinketHelper.getTrinkets(player, WallClimbingTrinket.class).forEach(trinketStack -> {
               if (trinketStack.isUsable(player)) {
                  event.setCanceled(true);
               }
            });
         }
      }
   }
}
