package iskallia.vault.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({CrossbowItem.class})
public class MixinCrossbowItem {
   @Redirect(
      method = {"fireProjectile"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/World;addEntity(Lnet/minecraft/entity/Entity;)Z"
      )
   )
   private static boolean applyEntityDamage(World world, Entity entity) {
      if (entity instanceof AbstractArrowEntity) {
         Entity shooter = ((AbstractArrowEntity)entity).func_234616_v_();
         if (shooter instanceof LivingEntity && !(shooter instanceof PlayerEntity)) {
            AttributeModifierManager mgr = ((LivingEntity)shooter).func_233645_dx_();
            if (mgr.func_233790_b_(Attributes.field_233823_f_)) {
               ((AbstractArrowEntity)entity).func_70239_b(mgr.func_233795_c_(Attributes.field_233823_f_));
            }
         }
      }

      return world.func_217376_c(entity);
   }
}
