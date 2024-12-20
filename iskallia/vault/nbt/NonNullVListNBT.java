package iskallia.vault.nbt;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class NonNullVListNBT<T, N extends Tag> extends VListNBT<T, N> {
   public NonNullVListNBT(List<T> list, Function<T, N> write, Function<N, T> read) {
      super(list, write, read);
   }

   public NonNullVListNBT(Function<T, N> write, Function<N, T> read) {
      super(write, read);
   }

   @Override
   public boolean add(T t) {
      return t == null ? false : super.add(t);
   }

   @Override
   public void add(int index, T element) {
      if (element != null) {
         super.add(index, element);
      }
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      return super.addAll(c.stream().filter(Objects::nonNull).toList());
   }

   @Override
   public boolean addAll(int index, Collection<? extends T> c) {
      return super.addAll(index, c.stream().filter(Objects::nonNull).toList());
   }

   @Override
   public T set(int index, T element) {
      return element == null ? null : super.set(index, element);
   }

   public static <T extends INBTSerializable<N>, N extends Tag> VListNBT<T, N> of(Function<N, T> read) {
      return new NonNullVListNBT<>(INBTSerializable::serializeNBT, read);
   }

   public static <T extends INBTSerializable<N>, N extends Tag> VListNBT<T, N> of(List<T> list, Function<N, T> read) {
      return new NonNullVListNBT<>(list, INBTSerializable::serializeNBT, read);
   }

   public static <T extends INBTSerializable<N>, N extends Tag> VListNBT<T, N> of(Supplier<T> supplier) {
      return new NonNullVListNBT<>(INBTSerializable::serializeNBT, n -> {
         T value = supplier.get();
         value.deserializeNBT(n);
         return value;
      });
   }

   public static VListNBT<UUID, StringTag> ofUUID() {
      return new NonNullVListNBT<>(uuid -> (N)StringTag.valueOf(uuid.toString()), stringNBT -> UUID.fromString(stringNBT.getAsString()));
   }
}
