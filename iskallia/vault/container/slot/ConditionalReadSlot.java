package iskallia.vault.container.slot;

import java.util.function.BiPredicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ConditionalReadSlot extends SlotItemHandler {
   private final BiPredicate<Integer, ItemStack> slotPredicate;

   public ConditionalReadSlot(IItemHandler inventory, int index, int xPosition, int yPosition, BiPredicate<Integer, ItemStack> slotPredicate) {
      super(inventory, index, xPosition, yPosition);
      this.slotPredicate = slotPredicate;
   }

   public boolean mayPlace(ItemStack stack) {
      return this.slotPredicate.test(this.getSlotIndex(), stack);
   }

   public boolean mayPickup(Player playerIn) {
      return this.slotPredicate.test(this.getSlotIndex(), this.getItem());
   }
}
