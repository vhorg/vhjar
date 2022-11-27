package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.data.WeightedList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.item.ItemStack;

public class ItemStackPool {
   public static final ItemStackPool EMPTY = new ItemStackPool(0, 0);
   @Expose
   private final WeightedList<ItemStackEntry> pool = new WeightedList<>();
   @Expose
   private final int minTotalStacks;
   @Expose
   private final int maxTotalStacks;

   public ItemStackPool(int minTotalStacks, int maxTotalStacks) {
      this.minTotalStacks = minTotalStacks;
      this.maxTotalStacks = maxTotalStacks;
   }

   public void addItemStack(ItemStack stack) {
      this.pool.add(new ItemStackEntry(stack, 1, 1), 1);
   }

   public void addItemStack(ItemStack stack, int count) {
      this.pool.add(new ItemStackEntry(stack, count, count), 1);
   }

   public void addItemStack(ItemStack stack, int min, int max) {
      this.pool.add(new ItemStackEntry(stack, min, max), 1);
   }

   public void addItemStack(ItemStack stack, int min, int max, int weight) {
      this.pool.add(new ItemStackEntry(stack, min, max), weight);
   }

   public List<WeightedList.Entry<ItemStackEntry>> getPool() {
      return Collections.unmodifiableList(this.pool);
   }

   public ItemStackEntry getRandomEntry() {
      return this.pool.copy().getRandom(Config.rand);
   }

   public int getRandomAmount() {
      return MathUtilities.getRandomInt(this.minTotalStacks, this.maxTotalStacks + 1);
   }

   public List<ItemStackEntry> getRandomEntries() {
      return IntStream.range(0, this.getRandomAmount()).mapToObj(index -> this.getRandomEntry()).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public List<ItemStack> getRandomStacks() {
      return this.getRandomEntries().stream().map(itemStackEntry -> itemStackEntry.createItemStack(Config.rand)).toList();
   }
}
