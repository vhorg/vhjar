package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;

public class GlobalDifficultyContainer extends Container {
   private CompoundNBT data;

   public GlobalDifficultyContainer(int windowId, CompoundNBT data) {
      super(ModContainers.GLOBAL_DIFFICULTY_CONTAINER, windowId);
      this.data = data;
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   public CompoundNBT getData() {
      return this.data;
   }
}
