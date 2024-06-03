package iskallia.vault.container.oversized;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class OverSizedInventory implements Container {
   public static final OverSizedInventory EMPTY = new OverSizedInventory(0, () -> {}, player -> false);
   private final NonNullList<OverSizedItemStack> contents;
   private final Runnable onChange;
   private final Predicate<Player> stillValid;

   public OverSizedInventory(int size, BlockEntity tile) {
      this(size, tile::setChanged, player -> stillValidTile().test(tile, player));
   }

   public OverSizedInventory(int size, Runnable onChange, Predicate<Player> stillValid) {
      this.contents = NonNullList.withSize(size, OverSizedItemStack.EMPTY);
      this.onChange = onChange;
      this.stillValid = stillValid;
   }

   public NonNullList<OverSizedItemStack> getOverSizedContents() {
      return this.contents;
   }

   public void setOverSizedStack(int slot, OverSizedItemStack stack) {
      this.contents.set(slot, stack);
   }

   public static BiPredicate<BlockEntity, Player> stillValidTile() {
      return (tile, player) -> tile.getLevel().getBlockEntity(tile.getBlockPos()) != tile
         ? false
         : player.distanceToSqr(tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5) <= 64.0;
   }

   public void load(CompoundTag tag) {
      this.load("items", tag);
   }

   public void load(String key, CompoundTag tag) {
      this.contents.clear();
      ListTag items = tag.getList(key, 10);

      for (int i = 0; i < items.size(); i++) {
         CompoundTag stackTag = items.getCompound(i);
         int slot = stackTag.getInt("slot");
         this.contents.set(slot, OverSizedItemStack.deserialize(stackTag.getCompound("stack")));
      }
   }

   public void save(CompoundTag tag) {
      this.save("items", tag);
   }

   public void save(String key, CompoundTag tag) {
      ListTag items = new ListTag();

      for (int i = 0; i < this.contents.size(); i++) {
         OverSizedItemStack stack = (OverSizedItemStack)this.contents.get(i);
         if (!stack.overSizedStack().isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            stackTag.putInt("slot", i);
            stackTag.put("stack", stack.serialize());
            items.add(stackTag);
         }
      }

      tag.put(key, items);
   }

   public List<ItemStack> getContents() {
      return this.contents.stream().map(OverSizedItemStack::overSizedStack).toList();
   }

   public int getContainerSize() {
      return this.contents.size();
   }

   public int getMaxStackSize() {
      return 2147483582;
   }

   public boolean isEmpty() {
      return this.contents.stream().mapToInt(OverSizedItemStack::amount).sum() <= 0;
   }

   public ItemStack getItem(int pIndex) {
      return ((OverSizedItemStack)this.contents.get(pIndex)).overSizedStack();
   }

   public ItemStack removeItem(int pIndex, int pCount) {
      if (pIndex >= 0 && pIndex < this.contents.size() && ((OverSizedItemStack)this.contents.get(pIndex)).amount() > 0 && pCount > 0) {
         ItemStack contained = ((OverSizedItemStack)this.contents.get(pIndex)).overSizedStack();
         ItemStack splitOff = contained.split(pCount);
         this.contents.set(pIndex, OverSizedItemStack.of(contained));
         this.setChanged();
         return splitOff;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack removeItemNoUpdate(int pIndex) {
      return pIndex >= 0 && pIndex < this.contents.size()
         ? ((OverSizedItemStack)this.contents.set(pIndex, OverSizedItemStack.EMPTY)).overSizedStack()
         : ItemStack.EMPTY;
   }

   public void setItem(int pIndex, ItemStack pStack) {
      this.contents.set(pIndex, OverSizedItemStack.of(pStack));
      this.setChanged();
   }

   public void setChanged() {
      this.onChange.run();
   }

   public boolean stillValid(Player pPlayer) {
      return this.stillValid.test(pPlayer);
   }

   public void clearContent() {
      this.contents.clear();
   }
}
