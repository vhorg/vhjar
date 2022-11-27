package iskallia.vault.mixin;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.Restrictions;
import iskallia.vault.research.StageManager;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.slot.VirtualCraftingOutputSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VirtualCraftingOutputSlot.class})
public abstract class MixinVirtualCraftingOutputSlot {
   @Inject(
      method = {"mayPickup"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void preventRestrictedOutput(Player player, CallbackInfoReturnable<Boolean> cir) {
      InventoryContainerSlot thisSlot = (InventoryContainerSlot)this;
      if (thisSlot.hasItem()) {
         ItemStack resultStack = thisSlot.getItem();
         ResearchTree researchTree = StageManager.getResearchTree(player);
         String restrictedBy = researchTree.restrictedBy(resultStack.getItem(), Restrictions.Type.CRAFTABILITY);
         if (restrictedBy != null) {
            cir.setReturnValue(false);
         }
      }
   }
}
