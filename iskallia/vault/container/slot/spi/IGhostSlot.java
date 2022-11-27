package iskallia.vault.container.slot.spi;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public interface IGhostSlot {
   @Nullable
   ItemStack getGhostItemStack();
}
