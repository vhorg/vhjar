package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ExperienceOrb.class})
public class MixinExperienceOrb {
   @Inject(
      method = {"repairPlayerItems"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void repairPlayerItems(Player p_147093_, int p_147094_, CallbackInfoReturnable<Integer> ci) {
      if (ServerVaults.get(p_147093_.level).isPresent()) {
         ci.setReturnValue(p_147094_);
      }
   }
}
