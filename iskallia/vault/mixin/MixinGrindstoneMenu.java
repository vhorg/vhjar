package iskallia.vault.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GrindstoneMenu.class})
public class MixinGrindstoneMenu {
   @Shadow
   @Final
   private Container resultSlots;

   @Inject(
      method = {"createResult"},
      at = {@At("RETURN")}
   )
   public void preventOverStackedResults(CallbackInfo ci) {
      Container resultContainer = this.resultSlots;
      ItemStack result = resultContainer.getItem(0);
      if (!result.isEmpty()) {
         if (result.getCount() > result.getMaxStackSize()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            AbstractContainerMenu thisContainer = (AbstractContainerMenu)this;
            thisContainer.broadcastChanges();
         }
      }
   }

   @Inject(
      method = {"removeNonCurses"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void reapplyBrokenDamage(ItemStack originalStack, int damage, int count, CallbackInfoReturnable<ItemStack> cir) {
      int damageValue = originalStack.getDamageValue();
      if (damageValue == -1) {
         ItemStack returnedStack = (ItemStack)cir.getReturnValue();
         returnedStack.setDamageValue(damageValue);
         cir.setReturnValue(returnedStack);
      }
   }
}
