package iskallia.vault.container.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;

public class OffhandTabSlot extends TabSlot {
   public OffhandTabSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
   }

   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
   }
}
