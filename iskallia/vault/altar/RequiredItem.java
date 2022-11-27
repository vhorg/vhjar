package iskallia.vault.altar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RequiredItem {
   private ItemStack item;
   private int currentAmount;
   private int amountRequired;

   public RequiredItem(ItemStack stack, int currentAmount, int amountRequired) {
      this.item = stack;
      this.currentAmount = currentAmount;
      this.amountRequired = amountRequired;
   }

   public RequiredItem(Item item, int currentAmount, int amountRequired) {
      this(new ItemStack(item), currentAmount, amountRequired);
   }

   public static CompoundTag serializeNBT(RequiredItem requiredItem) {
      CompoundTag nbt = new CompoundTag();
      nbt.put("item", requiredItem.getItem().serializeNBT());
      nbt.putInt("currentAmount", requiredItem.getCurrentAmount());
      nbt.putInt("amountRequired", requiredItem.getAmountRequired());
      return nbt;
   }

   public static RequiredItem deserializeNBT(CompoundTag nbt) {
      return !nbt.contains("item") ? null : new RequiredItem(ItemStack.of(nbt.getCompound("item")), nbt.getInt("currentAmount"), nbt.getInt("amountRequired"));
   }

   public ItemStack getItem() {
      return this.item;
   }

   public void setItem(ItemStack item) {
      this.item = item;
   }

   public int getCurrentAmount() {
      return this.currentAmount;
   }

   public void setCurrentAmount(int currentAmount) {
      this.currentAmount = currentAmount;
   }

   public void addAmount(int amount) {
      this.currentAmount += amount;
   }

   public int getAmountRequired() {
      return this.amountRequired;
   }

   public void setAmountRequired(int amountRequired) {
      this.amountRequired = amountRequired;
   }

   public boolean reachedAmountRequired() {
      return this.getCurrentAmount() >= this.getAmountRequired();
   }

   public int getRemainder(int amount) {
      return Math.max(this.getCurrentAmount() + amount - this.getAmountRequired(), 0);
   }

   public boolean isItemEqual(ItemStack stack) {
      return ItemStack.isSameIgnoreDurability(this.getItem(), stack);
   }

   public RequiredItem copy() {
      return new RequiredItem(this.item.copy(), this.currentAmount, this.amountRequired);
   }
}
