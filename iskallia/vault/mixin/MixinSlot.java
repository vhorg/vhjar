package iskallia.vault.mixin;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.Restrictions;
import iskallia.vault.research.StageManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Slot.class})
public abstract class MixinSlot {
   @Shadow
   public abstract ItemStack func_75211_c();

   @Shadow
   public abstract boolean func_75216_d();

   @Inject(
      method = {"canTakeStack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void preventRestrictedTake(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
      Slot thisSlot = (Slot)this;
      if (thisSlot instanceof CraftingResultSlot) {
         if (this.func_75216_d()) {
            ItemStack resultStack = this.func_75211_c();
            ResearchTree researchTree = StageManager.getResearchTree(player);
            String restrictedBy = researchTree.restrictedBy(resultStack.func_77973_b(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
               cir.setReturnValue(false);
            }
         }
      }
   }
}
