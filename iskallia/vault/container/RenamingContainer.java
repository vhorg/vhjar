package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import iskallia.vault.util.RenameType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;

public class RenamingContainer extends Container {
   private RenameType type;
   private CompoundNBT nbt;

   public RenamingContainer(int windowId, CompoundNBT nbt) {
      super(ModContainers.RENAMING_CONTAINER, windowId);
      this.type = RenameType.values()[nbt.func_74762_e("RenameType")];
      this.nbt = nbt.func_74775_l("Data");
   }

   public boolean func_75145_c(PlayerEntity playerIn) {
      return true;
   }

   public CompoundNBT getNbt() {
      return this.nbt;
   }

   public RenameType getRenameType() {
      return this.type;
   }
}
