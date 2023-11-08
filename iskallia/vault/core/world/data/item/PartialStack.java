package iskallia.vault.core.world.data.item;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class PartialStack implements ItemPlacement<PartialStack> {
   protected PartialItem item;
   protected PartialCompoundNbt nbt;

   protected PartialStack(PartialItem item, PartialCompoundNbt nbt) {
      this.item = item;
      this.nbt = nbt;
   }

   public static PartialStack of(PartialItem item, PartialCompoundNbt nbt) {
      return new PartialStack(item, nbt);
   }

   public static PartialStack of(ItemStack stack) {
      return new PartialStack(PartialItem.of(stack), PartialCompoundNbt.of(stack));
   }

   public PartialItem getItem() {
      return this.item;
   }

   public PartialCompoundNbt getNbt() {
      return this.nbt;
   }

   public void setItem(PartialItem item) {
      this.item = item;
   }

   public void setNbt(PartialCompoundNbt nbt) {
      this.nbt = nbt;
   }

   public boolean isSubsetOf(PartialStack other) {
      return !this.item.isSubsetOf(other.item) ? false : this.nbt.isSubsetOf(other.nbt);
   }

   @Override
   public boolean isSubsetOf(ItemStack stack) {
      return !this.item.isSubsetOf(stack) ? false : this.nbt.isSubsetOf(stack);
   }

   public void fillInto(PartialStack other) {
      this.item.fillInto(other.item);
      this.nbt.fillInto(other.nbt);
   }

   @Override
   public Optional<ItemStack> generate(int count) {
      return this.item.generate(count).map(stack -> {
         stack.setTag(this.nbt.asWhole().orElse(null));
         return (ItemStack)stack;
      });
   }

   @Override
   public boolean test(PartialItem item, PartialCompoundNbt nbt) {
      return this.isSubsetOf(of(item, nbt));
   }

   public PartialStack copy() {
      return new PartialStack(this.item.copy(), this.nbt.copy());
   }

   @Override
   public String toString() {
      return this.item.toString() + this.nbt.toString();
   }

   public static Optional<PartialStack> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialStack parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialStack parse(StringReader reader) throws CommandSyntaxException {
      return of(PartialItem.parse(reader), PartialCompoundNbt.parse(reader));
   }

   public static class Adapter implements ISimpleAdapter<PartialStack, Tag, JsonElement> {
      public Optional<Tag> writeNbt(@Nullable PartialStack value) {
         if (value == null) {
            return Optional.empty();
         } else {
            CompoundTag nbt = new CompoundTag();
            Adapters.PARTIAL_ITEM.writeNbt(value.item).ifPresent(tag -> nbt.put("item", tag));
            Adapters.PARTIAL_BLOCK_ENTITY.writeNbt(value.nbt).ifPresent(tag -> nbt.put("nbt", tag));
            return Optional.of(nbt);
         }
      }

      @Override
      public Optional<PartialStack> readNbt(@Nullable Tag nbt) {
         if (nbt instanceof CompoundTag compound) {
            PartialItem item = Adapters.PARTIAL_ITEM.readNbt(compound.get("item")).orElseThrow();
            PartialCompoundNbt tag = Adapters.PARTIAL_BLOCK_ENTITY.readNbt(compound.get("nbt")).orElseGet(PartialCompoundNbt::empty);
            return Optional.of(PartialStack.of(item, tag));
         } else {
            return Optional.empty();
         }
      }
   }
}
