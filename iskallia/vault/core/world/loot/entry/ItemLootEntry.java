package iskallia.vault.core.world.loot.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.VaultMod;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ItemLootEntry implements LootEntry {
   protected Item item;
   protected CompoundTag nbt;
   protected IntRoll count;

   public ItemLootEntry() {
      this(Items.AIR, null, IntRoll.ofConstant(1));
   }

   public ItemLootEntry(Item item, CompoundTag nbt, IntRoll count) {
      this.item = item;
      this.nbt = nbt;
      this.count = count;
   }

   public ItemLootEntry(Tag nbt) {
      this();
      this.readNbt(nbt);
   }

   public Item getItem() {
      return this.item;
   }

   public CompoundTag getNbt() {
      return this.nbt;
   }

   public IntRoll getCount() {
      return this.count;
   }

   @Override
   public List<ItemStack> getStack(RandomSource random) {
      List<ItemStack> items = new ArrayList<>();
      int count = this.count.get(random);
      ItemStack stack = new ItemStack(this.item);
      if (this.nbt != null) {
         stack.setTag(this.nbt.copy());
      }

      for (int i = 0; i < count; i += stack.getMaxStackSize()) {
         ItemStack copy = stack.copy();
         copy.setCount(Math.min(count - i, stack.getMaxStackSize()));
         items.add(copy);
      }

      return items;
   }

   @Override
   public OverSizedItemStack getOverStack(RandomSource random) {
      int count = this.count.get(random);
      ItemStack stack = new ItemStack(this.item);
      stack.setCount(count);
      if (this.nbt != null) {
         stack.setTag(this.nbt.copy());
      }

      return OverSizedItemStack.of(stack).copyAmount(count);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.ITEM.asNullable().writeBits((IForgeRegistryEntry)this.item, buffer);
      Adapters.COMPOUND_NBT.asNullable().writeBits(this.nbt, buffer);
      Adapters.INT_ROLL.writeBits(this.count, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.item = (Item)Adapters.ITEM.asNullable().readBits(buffer).orElse(null);
      this.nbt = Adapters.COMPOUND_NBT.asNullable().readBits(buffer).orElse(null);
      this.count = Adapters.INT_ROLL.readBits(buffer).orElse(null);
   }

   @Override
   public Optional<Tag> writeNbt() {
      if (this.nbt == null && this.count instanceof IntRoll.Constant constant && constant.getCount() == 1) {
         return Adapters.ITEM.writeNbt((IForgeRegistryEntry)this.item);
      } else {
         CompoundTag nbt = new CompoundTag();
         Adapters.ITEM.writeNbt((IForgeRegistryEntry)this.item).ifPresent(element -> nbt.put("id", element));
         Adapters.COMPOUND_NBT.writeNbt(this.nbt).ifPresent(element -> nbt.put("nbt", element));
         Adapters.INT_ROLL.writeNbt(this.count).ifPresent(element -> nbt.put("count", element));
         return Optional.of(nbt);
      }
   }

   @Override
   public void readNbt(Tag nbt) {
      if (nbt instanceof StringTag primitive) {
         Adapters.ITEM.readNbt(primitive).ifPresentOrElse(value -> this.item = value, () -> {
            VaultMod.LOGGER.error("Unknown item " + primitive);
            this.item = ModItems.ERROR_ITEM;
            if (this.nbt == null) {
               this.nbt = new CompoundTag();
            }

            this.nbt.putString("id", primitive.getAsString());
         });
      } else {
         if (!(nbt instanceof CompoundTag object)) {
            throw new UnsupportedOperationException(nbt + " cannot be read as an ItemLootEntry");
         }

         Adapters.COMPOUND_NBT.readNbt(object.get("nbt")).ifPresent(value -> this.nbt = value);
         Adapters.ITEM.readNbt(object.get("id")).ifPresentOrElse(value -> this.item = value, () -> {
            VaultMod.LOGGER.error("Unknown item " + object.get("id"));
            this.item = ModItems.ERROR_ITEM;
            if (this.nbt == null) {
               this.nbt = new CompoundTag();
            }

            if (object.contains("id")) {
               this.nbt.put("id", object.get("id"));
            }
         });
         Adapters.INT_ROLL.readNbt(object.get("count")).ifPresent(count -> this.count = count);
      }
   }

   @Override
   public Optional<JsonElement> writeJson() {
      if (this.nbt == null && this.count instanceof IntRoll.Constant constant && constant.getCount() == 1) {
         return Adapters.ITEM.writeJson((IForgeRegistryEntry)this.item);
      } else {
         JsonObject json = new JsonObject();
         Adapters.ITEM.writeJson((IForgeRegistryEntry)this.item).ifPresent(element -> json.add("id", element));
         Adapters.COMPOUND_NBT.writeJson(this.nbt).ifPresent(element -> json.add("nbt", element));
         Adapters.INT_ROLL.writeJson(this.count).ifPresent(element -> json.add("count", element));
         return Optional.of(json);
      }
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonPrimitive primitive && primitive.isString()) {
         Adapters.ITEM.readJson(primitive).ifPresentOrElse(value -> this.item = value, () -> {
            VaultMod.LOGGER.error("Unknown item " + primitive);
            this.item = ModItems.ERROR_ITEM;
            if (this.nbt == null) {
               this.nbt = new CompoundTag();
            }

            this.nbt.putString("id", primitive.getAsString());
         });
      } else {
         if (!(json instanceof JsonObject object)) {
            throw new UnsupportedOperationException(json + " cannot be read as an ItemLootEntry");
         }

         Adapters.COMPOUND_NBT.readJson(object.get("nbt")).ifPresent(value -> this.nbt = value);
         Adapters.ITEM.readJson(object.get("id")).ifPresentOrElse(value -> this.item = value, () -> {
            VaultMod.LOGGER.error("Unknown item " + object.get("id"));
            this.item = ModItems.ERROR_ITEM;
            if (this.nbt == null) {
               this.nbt = new CompoundTag();
            }

            this.nbt.putString("id", object.get("id").getAsString());
         });
         Adapters.INT_ROLL.readJson(object.get("count")).ifPresent(count -> this.count = count);
      }
   }

   @Override
   public LootEntry flatten(Version version, RandomSource random) {
      return this;
   }

   @Override
   public boolean validate() {
      return true;
   }
}
