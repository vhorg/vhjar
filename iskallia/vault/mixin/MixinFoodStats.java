package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModArchetypes;
import iskallia.vault.util.VHSmpUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FoodData.class})
public abstract class MixinFoodStats {
   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
      )},
      cancellable = true
   )
   public void preventNaturalHealing(Player player, CallbackInfo ci) {
      if (player.getFoodData().getFoodLevel() > 0 && (CommonEvents.PLAYER_REGEN.invoke(player, 1.0F).getAmount() <= 0.0F || VHSmpUtil.isArenaWorld(player))) {
         ci.cancel();
      }

      if (ModArchetypes.VAMPIRE.hasThisArchetype(player)) {
         ci.cancel();
      }
   }
}
