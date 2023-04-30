package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({AbstractSkeleton.class})
public class MixinAbstractSkeletonEntity {
   @Redirect(
      method = {"performRangedAttack"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   public boolean applySkeletonDamage(Level level, Entity entity) {
      if (ServerVaults.get(level).isEmpty()) {
         return level.addFreshEntity(entity);
      } else {
         AbstractSkeleton shooter = (AbstractSkeleton)this;
         AbstractArrow shot = (AbstractArrow)entity;
         double dmg = shooter.getAttributeValue(Attributes.ATTACK_DAMAGE);
         shot.setBaseDamage(dmg + 1.0 + shooter.getCommandSenderWorld().getDifficulty().getId() * 0.11);
         int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, shooter);
         if (power > 0) {
            shot.setBaseDamage(shot.getBaseDamage() + (power + 1) * 0.5);
         }

         return level.addFreshEntity(entity);
      }
   }
}
