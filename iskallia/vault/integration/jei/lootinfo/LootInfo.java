package iskallia.vault.integration.jei.lootinfo;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record LootInfo(List<ItemStack> itemStackList) {
}
