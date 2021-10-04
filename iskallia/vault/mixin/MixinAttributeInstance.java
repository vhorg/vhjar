package iskallia.vault.mixin;

import iskallia.vault.world.vault.modifier.FrenzyModifier;
import java.util.Collection;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ModifiableAttributeInstance.class})
public abstract class MixinAttributeInstance {
   @Shadow
   public abstract Collection<AttributeModifier> func_220370_b(Operation var1);

   @Redirect(
      method = {"computeValue"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/ai/attributes/Attribute;clampValue(D)D"
      )
   )
   private double computeValue(Attribute attribute, double value) {
      if (attribute == Attributes.field_233826_i_) {
         return MathHelper.func_151237_a(value, 0.0, 100.0);
      } else {
         if (attribute == Attributes.field_233818_a_) {
            boolean hasHealthSet = false;

            for (AttributeModifier modifier : this.func_220370_b(FrenzyModifier.FRENZY_HEALTH_OPERATION)) {
               if (FrenzyModifier.isFrenzyHealthModifier(modifier.func_111167_a())) {
                  hasHealthSet = true;
                  break;
               }
            }

            if (hasHealthSet) {
               return 1.0;
            }
         }

         return attribute.func_111109_a(value);
      }
   }
}
