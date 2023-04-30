package iskallia.vault.util.damage;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class CritHelper {
   public static boolean getCrit(Player player) {
      return AttackScaleHelper.getLastAttackScale(player) > 0.9F
         && player.fallDistance > 0.0F
         && !player.isOnGround()
         && !player.onClimbable()
         && !player.isInWater()
         && !player.hasEffect(MobEffects.BLINDNESS)
         && !player.isPassenger();
   }
}
