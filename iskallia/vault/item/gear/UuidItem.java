package iskallia.vault.item.gear;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;

public interface UuidItem {
   Optional<UUID> getUuid(ItemStack var1);
}
