package iskallia.vault.mixin;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.Restrictions;
import iskallia.vault.research.StageManager;
import java.util.List;
import mekanism.common.content.qio.QIOCraftingWindow;
import mekanism.common.inventory.container.slot.HotBarSlot;
import mekanism.common.inventory.container.slot.MainInventorySlot;
import mekanism.common.inventory.slot.CraftingWindowOutputInventorySlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({QIOCraftingWindow.class})
public class MixinQIOCraftingWindow {
   @Shadow
   @Final
   private CraftingWindowOutputInventorySlot outputSlot;

   @Inject(
      method = {"performCraft(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;Ljava/util/List;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lmekanism/common/content/qio/IQIOCraftingWindowHolder;getHolderWorld()Lnet/minecraft/world/level/Level;"
      )},
      cancellable = true,
      remap = false
   )
   public void preventShiftCrafting(Player player, List<HotBarSlot> hotBarSlots, List<MainInventorySlot> mainInventorySlots, CallbackInfo ci) {
      ItemStack resultStack = this.outputSlot.getStack().copy();
      ResearchTree researchTree = StageManager.getResearchTree(player);
      String restrictedBy = researchTree.restrictedBy(resultStack, Restrictions.Type.CRAFTABILITY);
      if (restrictedBy != null) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"performCraft(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void preventCrafting(Player player, ItemStack result, int amountCrafted, CallbackInfoReturnable<ItemStack> cir) {
      if (!result.isEmpty()) {
         ResearchTree researchTree = StageManager.getResearchTree(player);
         String restrictedBy = researchTree.restrictedBy(result, Restrictions.Type.CRAFTABILITY);
         if (restrictedBy != null) {
            cir.setReturnValue(ItemStack.EMPTY);
         }
      }
   }
}
