package iskallia.vault.mixin;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorMaterial.class})
public class MixinArmorMaterial {
   @Inject(
      method = {"getToughness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getToughness(CallbackInfoReturnable<Float> ci) {
      ArmorMaterial material = (ArmorMaterial)this;
      if (material == ArmorMaterial.LEATHER
         || material == ArmorMaterial.CHAIN
         || material == ArmorMaterial.GOLD
         || material == ArmorMaterial.IRON
         || material == ArmorMaterial.DIAMOND
         || material == ArmorMaterial.NETHERITE) {
         ci.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"getKnockbackResistance"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getKockbackResistance(CallbackInfoReturnable<Float> ci) {
      ArmorMaterial material = (ArmorMaterial)this;
      if (material == ArmorMaterial.LEATHER
         || material == ArmorMaterial.CHAIN
         || material == ArmorMaterial.GOLD
         || material == ArmorMaterial.IRON
         || material == ArmorMaterial.DIAMOND
         || material == ArmorMaterial.NETHERITE) {
         ci.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"getDamageReductionAmount"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getDamageReductionAmount(EquipmentSlotType slot, CallbackInfoReturnable<Integer> ci) {
      switch ((ArmorMaterial)this) {
         case LEATHER:
            switch (slot) {
               case HEAD:
                  ci.setReturnValue(1);
                  return;
               case CHEST:
                  ci.setReturnValue(1);
                  return;
               case LEGS:
                  ci.setReturnValue(1);
                  return;
               case FEET:
                  ci.setReturnValue(1);
                  return;
               default:
                  return;
            }
         case CHAIN:
            switch (slot) {
               case HEAD:
                  ci.setReturnValue(1);
                  return;
               case CHEST:
                  ci.setReturnValue(2);
                  return;
               case LEGS:
                  ci.setReturnValue(2);
                  return;
               case FEET:
                  ci.setReturnValue(1);
                  return;
               default:
                  return;
            }
         case GOLD:
            switch (slot) {
               case HEAD:
                  ci.setReturnValue(2);
                  return;
               case CHEST:
                  ci.setReturnValue(2);
                  return;
               case LEGS:
                  ci.setReturnValue(2);
                  return;
               case FEET:
                  ci.setReturnValue(1);
                  return;
               default:
                  return;
            }
         case IRON:
            switch (slot) {
               case HEAD:
                  ci.setReturnValue(2);
                  return;
               case CHEST:
                  ci.setReturnValue(2);
                  return;
               case LEGS:
                  ci.setReturnValue(2);
                  return;
               case FEET:
                  ci.setReturnValue(2);
                  return;
               default:
                  return;
            }
         case DIAMOND:
            switch (slot) {
               case HEAD:
                  ci.setReturnValue(2);
                  return;
               case CHEST:
                  ci.setReturnValue(3);
                  return;
               case LEGS:
                  ci.setReturnValue(3);
                  return;
               case FEET:
                  ci.setReturnValue(2);
                  return;
               default:
                  return;
            }
         case NETHERITE:
            switch (slot) {
               case HEAD:
                  ci.setReturnValue(3);
                  break;
               case CHEST:
                  ci.setReturnValue(4);
                  break;
               case LEGS:
                  ci.setReturnValue(4);
                  break;
               case FEET:
                  ci.setReturnValue(3);
            }
      }
   }
}
