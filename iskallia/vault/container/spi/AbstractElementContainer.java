package iskallia.vault.container.spi;

import iskallia.vault.container.slot.spi.IMovableSlot;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractElementContainer extends AbstractContainerMenu {
   protected final Player player;
   protected final List<IMovableSlot> movableSlotList;

   public AbstractElementContainer(MenuType<?> menuType, int id, Player player) {
      super(menuType, id);
      this.player = player;
      this.movableSlotList = new ArrayList<>();
   }

   public Player getPlayer() {
      return this.player;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSlotPositionOffset(int x, int y) {
      for (IMovableSlot slot : this.movableSlotList) {
         slot.setPositionOffset(x, y);
      }
   }

   @Nonnull
   public Slot addSlot(@Nonnull Slot slot) {
      if (slot instanceof IMovableSlot movableSlot) {
         this.movableSlotList.add(movableSlot);
      }

      return super.addSlot(slot);
   }

   protected boolean moveItemStackTo(@Nonnull ItemStack itemStack, AbstractElementContainer.SlotIndexRange slotIndexRange, boolean pReverseDirection) {
      return super.moveItemStackTo(itemStack, slotIndexRange.start(), slotIndexRange.end(), pReverseDirection);
   }

   public record SlotIndexRange(int start, int end) {
      public boolean contains(int index) {
         return index >= this.start && index < this.end;
      }

      public int getContainerIndex(int relativeIndex) {
         return this.start + relativeIndex;
      }
   }
}
