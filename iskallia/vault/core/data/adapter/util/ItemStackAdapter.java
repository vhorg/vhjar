package iskallia.vault.core.data.adapter.util;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class ItemStackAdapter implements ISimpleAdapter<ItemStack, Tag, JsonElement> {
   private final boolean nullable;

   public ItemStackAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ItemStackAdapter asNullable() {
      return new ItemStackAdapter(true);
   }

   public void writeBits(@Nullable ItemStack value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeBoolean(value.isEmpty());
         if (value.isEmpty()) {
            return;
         }

         Adapters.ITEM.writeBits((IForgeRegistryEntry)value.getItem(), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.getCount()), buffer);
         Adapters.COMPOUND_TAG.asNullable().writeBits(value.getTag(), buffer);
         CompoundTag caps = value.serializeNBT().getCompound("ForgeCaps");
         Adapters.COMPOUND_TAG.asNullable().writeBits(caps.isEmpty() ? null : caps, buffer);
      }
   }

   @Override
   public Optional<ItemStack> readBits(BitBuffer buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else if (buffer.readBoolean()) {
         return Optional.of(ItemStack.EMPTY);
      } else {
         Item item = Adapters.ITEM.readBits(buffer).orElse(Items.AIR);
         int count = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         CompoundTag tag = Adapters.COMPOUND_TAG.asNullable().readBits(buffer).orElse(null);
         CompoundTag caps = Adapters.COMPOUND_TAG.asNullable().readBits(buffer).orElse(null);
         ItemStack stack = new ItemStack(item, count, caps);
         stack.setTag(tag);
         return Optional.of(stack);
      }
   }

   public void writeBytes(@Nullable ItemStack value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeBoolean(value.isEmpty());
         if (value.isEmpty()) {
            return;
         }

         Adapters.ITEM.writeBytes((IForgeRegistryEntry)value.getItem(), buffer);
         Adapters.INT_SEGMENTED_7.writeBytes(Integer.valueOf(value.getCount()), buffer);
         Adapters.COMPOUND_TAG.asNullable().writeBytes(value.getTag(), buffer);
         CompoundTag caps = value.serializeNBT().getCompound("ForgeCaps");
         Adapters.COMPOUND_TAG.asNullable().writeBytes(caps.isEmpty() ? null : caps, buffer);
      }
   }

   @Override
   public Optional<ItemStack> readBytes(ByteBuf buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else if (buffer.readBoolean()) {
         return Optional.of(ItemStack.EMPTY);
      } else {
         Item item = Adapters.ITEM.readBytes(buffer).orElse(Items.AIR);
         int count = Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow();
         CompoundTag tag = Adapters.COMPOUND_TAG.asNullable().readBytes(buffer).orElse(null);
         CompoundTag caps = Adapters.COMPOUND_TAG.asNullable().readBytes(buffer).orElse(null);
         ItemStack stack = new ItemStack(item, count, caps);
         stack.setTag(tag);
         return Optional.of(stack);
      }
   }

   public void writeData(@Nullable ItemStack value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         data.writeBoolean(value.isEmpty());
         if (value.isEmpty()) {
            return;
         }

         Adapters.ITEM.writeData((IForgeRegistryEntry)value.getItem(), data);
         Adapters.INT_SEGMENTED_7.writeData(Integer.valueOf(value.getCount()), data);
         Adapters.COMPOUND_TAG.asNullable().writeData(value.getTag(), data);
         CompoundTag caps = value.serializeNBT().getCompound("ForgeCaps");
         Adapters.COMPOUND_TAG.asNullable().writeData(caps.isEmpty() ? null : caps, data);
      }
   }

   @Override
   public Optional<ItemStack> readData(DataInput data) throws IOException {
      if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else if (data.readBoolean()) {
         return Optional.of(ItemStack.EMPTY);
      } else {
         Item item = Adapters.ITEM.readData(data).orElse(Items.AIR);
         int count = Adapters.INT_SEGMENTED_7.readData(data).orElseThrow();
         CompoundTag tag = Adapters.COMPOUND_TAG.asNullable().readData(data).orElse(null);
         CompoundTag caps = Adapters.COMPOUND_TAG.asNullable().readData(data).orElse(null);
         ItemStack stack = new ItemStack(item, count, caps);
         stack.setTag(tag);
         return Optional.of(stack);
      }
   }

   public Optional<Tag> writeNbt(@Nullable ItemStack value) {
      return value == null ? Optional.empty() : Optional.of(value.serializeNBT());
   }

   @Override
   public Optional<ItemStack> readNbt(@Nullable Tag nbt) {
      return nbt instanceof CompoundTag compound ? Optional.of(ItemStack.of(compound)) : Optional.empty();
   }

   public Optional<JsonElement> writeJson(@Nullable ItemStack value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Optional<ItemStack> readJson(@Nullable JsonElement json) {
      throw new UnsupportedOperationException();
   }
}
