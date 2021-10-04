package iskallia.vault.container.slot;

import java.util.function.BiPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ConditionalReadSlot extends SlotItemHandler {
   private final BiPredicate<Integer, ItemStack> slotPredicate;

   public ConditionalReadSlot(IItemHandler inventory, int index, int xPosition, int yPosition, BiPredicate<Integer, ItemStack> slotPredicate) {
      super(inventory, index, xPosition, yPosition);
      this.slotPredicate = slotPredicate;
   }

   public boolean func_75214_a(ItemStack stack) {
      return this.slotPredicate.test(this.getSlotIndex(), stack);
   }

   public boolean func_82869_a(PlayerEntity playerIn) {
      return this.slotPredicate.test(this.getSlotIndex(), this.func_75211_c());
   }
}
