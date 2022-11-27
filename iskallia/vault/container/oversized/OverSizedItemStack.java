package iskallia.vault.container.oversized;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record OverSizedItemStack(ItemStack stack, int amount) {
   public static final OverSizedItemStack EMPTY = new OverSizedItemStack(ItemStack.EMPTY, 0);

   @Nonnull
   public static OverSizedItemStack of(ItemStack stack) {
      return new OverSizedItemStack(stack, stack.getCount());
   }

   public OverSizedItemStack copyAmount(int newAmount) {
      return new OverSizedItemStack(this.stack(), newAmount);
   }

   public OverSizedItemStack addCopy(int change) {
      return new OverSizedItemStack(this.stack(), this.amount() + change);
   }

   public ItemStack overSizedStack() {
      ItemStack overSized = this.stack().copy();
      overSized.setCount(this.amount());
      return overSized;
   }

   public List<ItemStack> splitByStackSize() {
      List<ItemStack> split = new ArrayList<>();
      ItemStack sample = this.stack();

      for (int i = 0; i < this.amount(); i += sample.getMaxStackSize()) {
         int amt = Math.min(this.amount() - i, sample.getMaxStackSize());
         ItemStack out = sample.copy();
         out.setCount(amt);
         split.add(out);
      }

      return split;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      ItemStack serializable = this.stack;
      if (this.amount() > 64) {
         serializable.setCount(64);
      }

      tag.put("stack", serializable.serializeNBT());
      tag.putInt("amount", this.amount());
      return tag;
   }

   public static OverSizedItemStack deserialize(CompoundTag tag) {
      ItemStack stack = ItemStack.of(tag.getCompound("stack"));
      return new OverSizedItemStack(stack, tag.getInt("amount"));
   }

   public void write(FriendlyByteBuf buf) {
      this.write(buf, true);
   }

   public void write(FriendlyByteBuf buf, boolean limitedTag) {
      if (this.amount() <= 0) {
         buf.writeBoolean(false);
      } else {
         buf.writeBoolean(true);
         Item item = this.stack().getItem();
         buf.writeVarInt(Item.getId(item));
         buf.writeInt(this.amount());
         CompoundTag compoundtag = null;
         if (item.isDamageable(this.stack()) || item.shouldOverrideMultiplayerNbt()) {
            compoundtag = limitedTag ? this.stack().getShareTag() : this.stack().getTag();
         }

         buf.writeNbt(compoundtag);
      }
   }

   public static OverSizedItemStack read(FriendlyByteBuf buf) {
      if (!buf.readBoolean()) {
         return EMPTY;
      } else {
         int itemId = buf.readVarInt();
         int count = buf.readInt();
         ItemStack itemstack = new ItemStack(Item.byId(itemId), Math.min(count, 64));
         itemstack.readShareTag(buf.readNbt());
         return new OverSizedItemStack(itemstack, count);
      }
   }
}
