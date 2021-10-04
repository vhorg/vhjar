package iskallia.vault.util.calc;

import net.minecraft.entity.player.PlayerEntity;

public class AttributeLimitHelper {
   public static float getCooldownReductionLimit(PlayerEntity player) {
      return 0.8F;
   }

   public static float getParryLimit(PlayerEntity player) {
      return 0.8F;
   }

   public static float getResistanceLimit(PlayerEntity player) {
      return 0.5F;
   }
}
