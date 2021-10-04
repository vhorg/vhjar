package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class OmegaStatueContainer extends Container {
   private CompoundNBT data;

   public OmegaStatueContainer(int windowId, CompoundNBT nbt) {
      super(ModContainers.OMEGA_STATUE_CONTAINER, windowId);
      this.data = nbt;
   }

   public boolean func_75145_c(PlayerEntity playerIn) {
      return true;
   }

   public ListNBT getItemsCompound() {
      return this.data.func_150295_c("Items", 10);
   }

   public CompoundNBT getBlockPos() {
      return this.data.func_74775_l("Position");
   }
}
