package iskallia.vault.core.world.loot.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
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
   public ItemStack getStack(RandomSource random) {
      ItemStack stack = new ItemStack(this.item, this.count.get(random));
      if (this.nbt != null) {
         stack.setTag(this.nbt.copy());
      }

      return stack;
   }

   @Override
   public Optional<JsonElement> writeJson() {
      if (this.nbt == null && this.count instanceof IntRoll.Constant constant && constant.getCount() == 1) {
         return Adapters.ITEM.writeJson((IForgeRegistryEntry)this.item);
      } else {
         JsonObject json = new JsonObject();
         Adapters.ITEM.writeJson((IForgeRegistryEntry)this.item).ifPresent(element -> json.add("item", element));
         Adapters.COMPOUND_NBT.writeJson(this.nbt).ifPresent(element -> json.add("nbt", element));
         Adapters.INT_ROLL.writeJson(this.count).ifPresent(element -> json.add("count", element));
         return Optional.of(json);
      }
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonPrimitive primitive && primitive.isString()) {
         Adapters.ITEM
            .readJson(primitive)
            .ifPresentOrElse(value -> this.item = value, () -> VaultMod.LOGGER.error("Unknown item " + primitive + ", using air instead"));
      } else {
         if (!(json instanceof JsonObject object)) {
            throw new UnsupportedOperationException(json + " cannot be read as an ItemLootEntry");
         }

         Adapters.ITEM
            .readJson(object.get("id"))
            .ifPresentOrElse(value -> this.item = value, () -> VaultMod.LOGGER.error("Unknown item " + object.get("id") + ", using air instead"));
         Adapters.COMPOUND_NBT.readJson(object.get("nbt")).ifPresent(value -> this.nbt = value);
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
