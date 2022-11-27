package iskallia.vault.container.slot;

import iskallia.vault.container.slot.spi.IMovableSlot;
import iskallia.vault.mixin.AccessorSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class CurioTabSlot extends CurioSlot implements IMovableSlot {
   private final int originX;
   private final int originY;

   public CurioTabSlot(Player player, IDynamicStackHandler handler, int index, String identifier, int xPosition, int yPosition, NonNullList<Boolean> renders) {
      super(player, handler, index, identifier, xPosition, yPosition, renders);
      this.originX = xPosition;
      this.originY = yPosition;
   }

   @Override
   public void setPositionOffset(int x, int y) {
      ((AccessorSlot)this).setX(this.originX + x);
      ((AccessorSlot)this).setY(this.originY + y);
   }
}
