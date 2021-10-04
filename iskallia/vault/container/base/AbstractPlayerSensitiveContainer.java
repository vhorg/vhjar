package iskallia.vault.container.base;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractPlayerSensitiveContainer extends Container implements PlayerSensitiveContainer {
   private int dragMode = -1;
   private int dragEvent;
   private final Set<Slot> dragSlots = Sets.newHashSet();

   protected AbstractPlayerSensitiveContainer(@Nullable ContainerType<?> type, int id) {
      super(type, id);
   }

   @Override
   public void setDragMode(int dragMode) {
      this.dragMode = dragMode;
   }

   @Override
   public int getDragMode() {
      return this.dragMode;
   }

   @Override
   public void setDragEvent(int dragEvent) {
      this.dragEvent = dragEvent;
   }

   @Override
   public int getDragEvent() {
      return this.dragEvent;
   }

   @Override
   public Set<Slot> getDragSlots() {
      return this.dragSlots;
   }

   public void func_94533_d() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public ItemStack func_184996_a(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
      return this.playerSensitiveSlotClick(this, slotId, dragType, clickType, player);
   }
}
