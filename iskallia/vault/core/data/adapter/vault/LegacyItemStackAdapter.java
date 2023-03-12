package iskallia.vault.core.data.adapter.vault;

import com.google.gson.JsonElement;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class LegacyItemStackAdapter implements ISimpleAdapter<ItemStack, Tag, JsonElement> {
   public static final LegacyItemStackAdapter INSTANCE = new LegacyItemStackAdapter();

   public void writeBits(@NotNull ItemStack value, BitBuffer buffer) {
      buffer.writeBoolean(value.isEmpty());
      if (!value.isEmpty()) {
         Adapters.ITEM.asNullable().writeBits((IForgeRegistryEntry)value.getItem(), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.getCount()), buffer);
         LegacyNbtAdapter.COMPOUND.asNullable().writeBits((Tag)value.getTag(), buffer);
         CompoundTag caps = value.serializeNBT().getCompound("ForgeCaps");
         LegacyNbtAdapter.COMPOUND.asNullable().writeBits((Tag)(caps.isEmpty() ? null : caps), buffer);
      }
   }

   @Override
   public Optional<ItemStack> readBits(BitBuffer buffer) {
      if (buffer.readBoolean()) {
         return Optional.of(ItemStack.EMPTY);
      } else {
         Item item = Adapters.ITEM.asNullable().readBits(buffer).orElse(Items.AIR);
         int count = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         CompoundTag tag = (CompoundTag)LegacyNbtAdapter.COMPOUND.asNullable().readBits(buffer).orElse(null);
         CompoundTag caps = (CompoundTag)LegacyNbtAdapter.COMPOUND.asNullable().readBits(buffer).orElse(null);
         ItemStack stack = new ItemStack(item, count, caps);
         stack.setTag(tag);
         return Optional.of(stack);
      }
   }
}
