package iskallia.vault.mixin;

import iskallia.vault.init.ModAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Mob.class})
public abstract class MixinMob extends LivingEntity {
   protected MixinMob(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
      super(p_20966_, p_20967_);
   }

   @Inject(
      method = {"getMeleeAttackRangeSqr"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getMeleeAttackRangeSqr(LivingEntity p_147273_, CallbackInfoReturnable<Double> ci) {
      AttributeInstance attribute = this.getAttribute(ModAttributes.REACH);
      if (attribute != null && attribute.getValue() > 0.0) {
         ci.setReturnValue(attribute.getValue() * attribute.getValue());
      }
   }
}
