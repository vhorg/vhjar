package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.SmallFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({SmallFireball.class})
public class MixinSmallFireball {
   @Redirect(
      method = {"onHitEntity"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )
   )
   public boolean transferAttackDamage(Entity target, DamageSource src, float damage) {
      if (src.getEntity() instanceof LivingEntity livingEntity && ServerVaults.isInVault(livingEntity)) {
         AttributeInstance inst = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
         if (inst != null) {
            damage = (float)inst.getValue();
         }
      }

      return target.hurt(src, damage);
   }
}
