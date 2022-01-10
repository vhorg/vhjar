package iskallia.vault.mixin;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.Restrictions;
import iskallia.vault.research.StageManager;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.slot.VirtualCraftingOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VirtualCraftingOutputSlot.class})
public abstract class MixinVirtualCraftingOutputSlot {
   @Inject(
      method = {"canTakeStack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void preventRestrictedOutput(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
      InventoryContainerSlot thisSlot = (InventoryContainerSlot)this;
      if (thisSlot.func_75216_d()) {
         ItemStack resultStack = thisSlot.func_75211_c();
         ResearchTree researchTree = StageManager.getResearchTree(player);
         String restrictedBy = researchTree.restrictedBy(resultStack.func_77973_b(), Restrictions.Type.CRAFTABILITY);
         if (restrictedBy != null) {
            cir.setReturnValue(false);
         }
      }
   }
}
