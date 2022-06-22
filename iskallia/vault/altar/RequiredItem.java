package iskallia.vault.altar;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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

   public static CompoundNBT serializeNBT(RequiredItem requiredItem) {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_218657_a("item", requiredItem.getItem().serializeNBT());
      nbt.func_74768_a("currentAmount", requiredItem.getCurrentAmount());
      nbt.func_74768_a("amountRequired", requiredItem.getAmountRequired());
      return nbt;
   }

   public static RequiredItem deserializeNBT(CompoundNBT nbt) {
      return !nbt.func_74764_b("item")
         ? null
         : new RequiredItem(ItemStack.func_199557_a(nbt.func_74775_l("item")), nbt.func_74762_e("currentAmount"), nbt.func_74762_e("amountRequired"));
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
      return ItemStack.func_185132_d(this.getItem(), stack);
   }

   public RequiredItem copy() {
      return new RequiredItem(this.item.func_77946_l(), this.currentAmount, this.amountRequired);
   }
}
