package iskallia.vault.item.gear;

import iskallia.vault.config.VaultRecyclerConfig;
import net.minecraft.world.item.ItemStack;

public interface RecyclableItem extends UuidItem {
   boolean isValidInput(ItemStack var1);

   VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack var1);

   float getResultPercentage(ItemStack var1);
}
