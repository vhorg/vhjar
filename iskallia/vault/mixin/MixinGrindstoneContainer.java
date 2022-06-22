package iskallia.vault.mixin;

import iskallia.vault.item.gear.VaultGear;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GrindstoneContainer.class})
public class MixinGrindstoneContainer {
   @Shadow
   @Final
   private IInventory field_217013_c;
   @Shadow
   @Final
   private IInventory field_217014_d;

   @Inject(
      method = {"updateRecipeOutput"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void outputEmptyOnVaultGears(CallbackInfo ci) {
      GrindstoneContainer container = (GrindstoneContainer)this;
      ItemStack topStack = this.field_217014_d.func_70301_a(0);
      ItemStack bottomStack = this.field_217014_d.func_70301_a(1);
      if (topStack.func_77973_b() instanceof VaultGear || bottomStack.func_77973_b() instanceof VaultGear) {
         this.field_217013_c.func_70299_a(0, ItemStack.field_190927_a);
         container.func_75142_b();
         ci.cancel();
      }
   }
}
