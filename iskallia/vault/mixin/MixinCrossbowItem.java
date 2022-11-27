package iskallia.vault.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({CrossbowItem.class})
public class MixinCrossbowItem {
   @Redirect(
      method = {"shootProjectile"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   private static boolean applyEntityDamage(Level world, Entity entity) {
      if (entity instanceof AbstractArrow) {
         Entity shooter = ((AbstractArrow)entity).getOwner();
         if (shooter instanceof LivingEntity && !(shooter instanceof Player)) {
            AttributeMap mgr = ((LivingEntity)shooter).getAttributes();
            if (mgr.hasAttribute(Attributes.ATTACK_DAMAGE)) {
               ((AbstractArrow)entity).setBaseDamage(mgr.getValue(Attributes.ATTACK_DAMAGE));
            }
         }
      }

      return world.addFreshEntity(entity);
   }
}
