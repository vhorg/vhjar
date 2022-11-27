package iskallia.vault.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorMaterials.class})
public class MixinArmorMaterial {
   @Inject(
      method = {"getToughness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getToughness(CallbackInfoReturnable<Float> ci) {
      ArmorMaterials material = (ArmorMaterials)this;
      if (material == ArmorMaterials.LEATHER
         || material == ArmorMaterials.CHAIN
         || material == ArmorMaterials.GOLD
         || material == ArmorMaterials.IRON
         || material == ArmorMaterials.DIAMOND
         || material == ArmorMaterials.NETHERITE) {
         ci.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"getKnockbackResistance"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getKockbackResistance(CallbackInfoReturnable<Float> ci) {
      ArmorMaterials material = (ArmorMaterials)this;
      if (material == ArmorMaterials.LEATHER
         || material == ArmorMaterials.CHAIN
         || material == ArmorMaterials.GOLD
         || material == ArmorMaterials.IRON
         || material == ArmorMaterials.DIAMOND
         || material == ArmorMaterials.NETHERITE) {
         ci.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"getDefenseForSlot"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getDamageReductionAmount(EquipmentSlot slot, CallbackInfoReturnable<Integer> ci) {
      switch ((ArmorMaterials)this) {
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
