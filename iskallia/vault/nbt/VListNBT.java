package iskallia.vault.nbt;

import com.mojang.serialization.Codec;
import iskallia.vault.util.CodecUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class VListNBT<T, N extends Tag> implements INBTSerializable<ListTag>, List<T> {
   private List<T> delegate;
   private final Function<T, N> write;
   private final Function<N, T> read;

   public VListNBT(List<T> list, Function<T, N> write, Function<N, T> read) {
      this.delegate = list;
      this.write = write;
      this.read = read;
   }

   public VListNBT(Function<T, N> write, Function<N, T> read) {
      this(new ArrayList<>(), write, read);
   }

   public ListTag serializeNBT() {
      ListTag nbt = new ListTag();
      this.delegate.forEach(value -> nbt.add(this.write.apply((T)value)));
      return nbt;
   }

   public void deserializeNBT(ListTag nbt) {
      this.delegate.clear();
      nbt.stream().map(tag -> (Tag)tag).forEach(entry -> this.add(this.read.apply((N)entry)));
   }

   @Override
   public int size() {
      return this.delegate.size();
   }

   @Override
   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.delegate.contains(o);
   }

   @Override
   public Iterator<T> iterator() {
      return this.delegate.iterator();
   }

   @Override
   public Object[] toArray() {
      return this.delegate.toArray();
   }

   @Override
   public <T1> T1[] toArray(T1[] a) {
      return this.delegate.toArray(a);
   }

   @Override
   public boolean add(T t) {
      return this.delegate.add(t);
   }

   @Override
   public boolean remove(Object o) {
      return this.delegate.remove(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return this.delegate.containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      return this.delegate.addAll(c);
   }

   @Override
   public boolean addAll(int index, Collection<? extends T> c) {
      return this.delegate.addAll(index, c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return this.delegate.removeAll(c);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return this.delegate.retainAll(c);
   }

   @Override
   public void clear() {
      this.delegate.clear();
   }

   @Override
   public T get(int index) {
      return this.delegate.get(index);
   }

   @Override
   public T set(int index, T element) {
      return this.delegate.set(index, element);
   }

   @Override
   public void add(int index, T element) {
      this.delegate.add(index, element);
   }

   @Override
   public T remove(int index) {
      return this.delegate.remove(index);
   }

   @Override
   public int indexOf(Object o) {
      return this.delegate.indexOf(o);
   }

   @Override
   public int lastIndexOf(Object o) {
      return this.delegate.lastIndexOf(o);
   }

   @Override
   public ListIterator<T> listIterator() {
      return this.delegate.listIterator();
   }

   @Override
   public ListIterator<T> listIterator(int index) {
      return this.delegate.listIterator(index);
   }

   @Override
   public List<T> subList(int fromIndex, int toIndex) {
      return this.delegate.subList(fromIndex, toIndex);
   }

   public static <T extends INBTSerializable<N>, N extends Tag> VListNBT<T, N> of(Function<N, T> read) {
      return new VListNBT<>(INBTSerializable::serializeNBT, read);
   }

   public static <T extends INBTSerializable<N>, N extends Tag> VListNBT<T, N> of(List<T> list, Function<N, T> read) {
      return new VListNBT<>(list, INBTSerializable::serializeNBT, read);
   }

   public static <T extends INBTSerializable<N>, N extends Tag> VListNBT<T, N> of(Supplier<T> supplier) {
      return new VListNBT<>(INBTSerializable::serializeNBT, n -> {
         T value = supplier.get();
         value.deserializeNBT(n);
         return value;
      });
   }

   public static VListNBT<UUID, StringTag> ofUUID() {
      return new VListNBT<>(uuid -> (N)StringTag.valueOf(uuid.toString()), stringNBT -> UUID.fromString(stringNBT.getAsString()));
   }

   public static <T> VListNBT<T, CompoundTag> ofCodec(Codec<T> codec, T defaultValue) {
      return new VListNBT<>(value -> {
         CompoundTag tag = new CompoundTag();
         tag.put("data", CodecUtils.writeNBT(codec, value));
         return (N)tag;
      }, tag -> CodecUtils.<T>readNBT(codec, tag.get("data")).orElse(defaultValue));
   }
}
