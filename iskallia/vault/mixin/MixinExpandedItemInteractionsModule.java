package iskallia.vault.mixin;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.content.management.module.ExpandedItemInteractionsModule;

@Mixin({ExpandedItemInteractionsModule.class})
public class MixinExpandedItemInteractionsModule {
   @Redirect(
      method = {"armorOverride"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;",
         ordinal = 0
      )
   )
   private static Item returnAirForUnidentifiedGear(ItemStack stack) {
      if (stack.getItem() instanceof VaultGearItem) {
         VaultGearData data = VaultGearData.read(stack);
         if (data.getState() != VaultGearState.IDENTIFIED) {
            return Items.AIR;
         }
      }

      return stack.getItem();
   }
}
