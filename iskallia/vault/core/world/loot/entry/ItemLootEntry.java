package iskallia.vault.core.world.loot.entry;

import iskallia.vault.core.Version;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemLootEntry implements LootEntry {
   protected Item item;
   protected CompoundTag nbt;
   protected IntRoll count;

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
   public LootEntry flatten(Version version, RandomSource random) {
      return this;
   }

   @Override
   public boolean validate() {
      return true;
   }
}
