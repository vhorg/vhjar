package iskallia.vault.core.world.loot.entry;

import com.google.gson.JsonElement;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public interface LootEntry extends ISerializable<Tag, JsonElement> {
   List<ItemStack> getStack(RandomSource var1);

   OverSizedItemStack getOverStack(RandomSource var1);

   LootEntry flatten(Version var1, RandomSource var2);

   boolean validate();
}
