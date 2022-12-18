package iskallia.vault.altar;

import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class RequiredItems implements INBTSerializable<CompoundTag> {
   private String poolId;
   private final List<ItemStack> items = new ArrayList<>();
   private int currentAmount;
   private int amountRequired;

   public RequiredItems(String poolId, List<ItemStack> items, int amountRequired) {
      this.poolId = poolId;
      this.items.addAll(items);
      this.amountRequired = amountRequired;
   }

   public RequiredItems(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public RequiredItems(String poolId, List<ItemStack> items, int currentAmount, int amountRequired) {
      this.poolId = poolId;
      this.items.addAll(items);
      this.currentAmount = currentAmount;
      this.amountRequired = amountRequired;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("poolId", this.poolId == null ? UUID.randomUUID().toString() : this.poolId);
      NBTHelper.writeCollection(nbt, "items", this.items, CompoundTag.class, IForgeItemStack::serializeNBT);
      nbt.putInt("currentAmount", this.getCurrentAmount());
      nbt.putInt("amountRequired", this.getAmountRequired());
      return (R)nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      if (nbt.contains("poolId")) {
         this.poolId = nbt.getString("poolId");
      } else {
         this.poolId = UUID.randomUUID().toString();
      }

      if (nbt.contains("items")) {
         this.items.addAll(NBTHelper.readList(nbt, "items", CompoundTag.class, ItemStack::of));
      }

      if (nbt.contains("item")) {
         this.items.add(ItemStack.of(nbt.getCompound("item")));
      }

      this.currentAmount = nbt.getInt("currentAmount");
      this.amountRequired = nbt.getInt("amountRequired");
   }

   public void addStack(ItemStack stack) {
      this.items.add(stack);
   }

   public List<ItemStack> getItems() {
      return this.items;
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

   public boolean isComplete() {
      return this.getCurrentAmount() >= this.getAmountRequired();
   }

   public int getRemainder(int amount) {
      return Math.max(this.getCurrentAmount() + amount - this.getAmountRequired(), 0);
   }

   public RequiredItems copy() {
      return new RequiredItems(this.poolId, this.items, this.currentAmount, this.amountRequired);
   }

   public String getPoolId() {
      return this.poolId;
   }
}
