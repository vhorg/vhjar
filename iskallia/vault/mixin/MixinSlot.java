package iskallia.vault.mixin;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.Restrictions;
import iskallia.vault.research.StageManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Slot.class})
public abstract class MixinSlot {
   @Shadow
   public abstract boolean hasItem();

   @Shadow
   public abstract ItemStack getItem();

   @Inject(
      method = {"mayPickup"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void preventRestrictedTake(Player player, CallbackInfoReturnable<Boolean> cir) {
      Slot thisSlot = (Slot)this;
      if (thisSlot instanceof ResultSlot) {
         if (this.hasItem()) {
            ItemStack resultStack = this.getItem();
            ResearchTree researchTree = StageManager.getResearchTree(player);
            String restrictedBy = researchTree.restrictedBy(resultStack, Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
               cir.setReturnValue(false);
            }
         }
      }
   }
}
