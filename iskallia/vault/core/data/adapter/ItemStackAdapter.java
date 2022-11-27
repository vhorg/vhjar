package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStackAdapter extends Adapter<ItemStack> {
   public static final IdentifierAdapter ITEM = Adapter.ofIdentifier().asNullable();
   public static final NBTAdapter<CompoundTag> TAG = Adapter.ofNBT(CompoundTag.class).asNullable();

   public ItemStack validate(ItemStack value, SyncContext context) {
      if (value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, ItemStack value) {
      buffer.writeBoolean(value.isEmpty());
      if (!value.isEmpty()) {
         ResourceLocation id = ForgeRegistries.ITEMS.getKey(value.getItem());
         ITEM.writeValue(buffer, context, id);
         buffer.writeIntSegmented(value.getCount(), 7);
         TAG.writeValue(buffer, context, (Tag)value.getTag());
         CompoundTag caps = value.save(new CompoundTag()).getCompound("ForgeCaps");
         TAG.writeValue(buffer, context, (Tag)(caps.isEmpty() ? null : caps));
      }
   }

   public ItemStack readValue(BitBuffer buffer, SyncContext context, ItemStack value) {
      if (buffer.readBoolean()) {
         return ItemStack.EMPTY;
      } else {
         ResourceLocation id = ITEM.readValue(buffer, context, null);
         Item item = id == null ? Items.AIR : (Item)ForgeRegistries.ITEMS.getValue(id);
         int count = buffer.readIntSegmented(7);
         CompoundTag tag = (CompoundTag)TAG.readValue(buffer, context, null);
         CompoundTag caps = (CompoundTag)TAG.readValue(buffer, context, null);
         ItemStack stack = new ItemStack(item, count, caps);
         stack.setTag(tag);
         return stack;
      }
   }
}
