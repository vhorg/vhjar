package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import iskallia.vault.util.RenameType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RenamingContainer extends AbstractContainerMenu {
   private final RenameType type;
   private final CompoundTag nbt;

   public RenamingContainer(int windowId, CompoundTag nbt) {
      super(ModContainers.RENAMING_CONTAINER, windowId);
      this.type = RenameType.values()[nbt.getInt("RenameType")];
      this.nbt = nbt.getCompound("Data");
   }

   public boolean stillValid(Player playerIn) {
      return true;
   }

   public CompoundTag getNbt() {
      return this.nbt;
   }

   public RenameType getRenameType() {
      return this.type;
   }
}
