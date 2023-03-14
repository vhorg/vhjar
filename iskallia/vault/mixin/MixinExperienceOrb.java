package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ExperienceOrb.class})
public abstract class MixinExperienceOrb extends Entity {
   @Shadow
   public int value;

   public MixinExperienceOrb(EntityType<?> type, Level world) {
      super(type, world);
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void playerTouch(Player entity, CallbackInfo ci) {
      if (!entity.level.isClientSide && ServerVaults.get(entity.level).isPresent()) {
         entity.take(this, 1);
         if (this.value > 0) {
            entity.giveExperiencePoints(this.value);
         }

         this.discard();
         ci.cancel();
      }
   }
}
