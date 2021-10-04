package iskallia.vault.mixin;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FoodStats.class})
public class MixinFoodStats {
   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;shouldHeal()Z"
      )
   )
   public boolean shouldHeal(PlayerEntity player) {
      if (!player.field_70170_p.field_72995_K) {
         VaultRaid vault = VaultRaidData.get((ServerWorld)player.field_70170_p).getActiveFor(player.func_110124_au());
         if (vault != null) {
            return false;
         }
      }

      return player.func_70996_bM();
   }
}
