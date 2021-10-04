package iskallia.vault.container.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

public interface PlayerSensitiveSlot {
   default ItemStack modifyTakenStack(PlayerEntity player, ItemStack taken, boolean simulate) {
      return this.modifyTakenStack(player, taken, player.func_130014_f_().func_201670_d() ? LogicalSide.CLIENT : LogicalSide.SERVER, simulate);
   }

   ItemStack modifyTakenStack(PlayerEntity var1, ItemStack var2, LogicalSide var3, boolean var4);
}
