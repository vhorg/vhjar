package iskallia.vault.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({AbstractSkeletonEntity.class})
public class MixinAbstractSkeletonEntity {
   @Redirect(
      method = {"attackEntityWithRangedAttack"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/World;addEntity(Lnet/minecraft/entity/Entity;)Z"
      )
   )
   public boolean applySkeletonDamage(World world, Entity entityIn) {
      AbstractSkeletonEntity shooter = (AbstractSkeletonEntity)this;
      AbstractArrowEntity shot = (AbstractArrowEntity)entityIn;
      double dmg = shooter.func_233637_b_(Attributes.field_233823_f_);
      shot.func_70239_b(dmg + 1.0 + shooter.func_130014_f_().func_175659_aa().func_151525_a() * 0.11);
      int power = EnchantmentHelper.func_185284_a(Enchantments.field_185309_u, shooter);
      if (power > 0) {
         shot.func_70239_b(shot.func_70242_d() + (power + 1) * 0.5);
      }

      return world.func_217376_c(entityIn);
   }
}
