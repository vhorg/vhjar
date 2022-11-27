package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class LootStatueContainer extends AbstractContainerMenu {
   private final CompoundTag data;

   public LootStatueContainer(int windowId, CompoundTag nbt) {
      super(ModContainers.LOOT_STATUE_CONTAINER, windowId);
      this.data = nbt;
   }

   public boolean stillValid(Player playerIn) {
      return true;
   }

   public ListTag getItemsCompound() {
      return this.data.getList("Items", 10);
   }

   public CompoundTag getBlockPos() {
      return this.data.getCompound("Position");
   }
}
