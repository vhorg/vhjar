package iskallia.vault.nbt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class VMapNBT<K, V> implements INBTSerializable<ListNBT>, Map<K, V> {
   private Map<K, V> delegate;
   private final BiConsumer<CompoundNBT, K> writeKey;
   private final BiConsumer<CompoundNBT, V> writeValue;
   private final Function<CompoundNBT, K> readKey;
   private final Function<CompoundNBT, V> readValue;

   public VMapNBT(
      Map<K, V> map,
      BiConsumer<CompoundNBT, K> writeKey,
      BiConsumer<CompoundNBT, V> writeValue,
      Function<CompoundNBT, K> readKey,
      Function<CompoundNBT, V> readValue
   ) {
      this.delegate = map;
      this.writeKey = writeKey;
      this.writeValue = writeValue;
      this.readKey = readKey;
      this.readValue = readValue;
   }

   public VMapNBT(
      BiConsumer<CompoundNBT, K> writeKey, BiConsumer<CompoundNBT, V> writeValue, Function<CompoundNBT, K> readKey, Function<CompoundNBT, V> readValue
   ) {
      this(new HashMap<>(), writeKey, writeValue, readKey, readValue);
   }

   public ListNBT serializeNBT() {
      ListNBT nbt = new ListNBT();
      this.delegate.forEach((key, value) -> {
         CompoundNBT entry = new CompoundNBT();
         this.writeKey.accept(entry, (K)key);
         this.writeValue.accept(entry, (V)value);
         nbt.add(entry);
      });
      return nbt;
   }

   public void deserializeNBT(ListNBT nbt) {
      this.delegate.clear();
      IntStream.range(0, nbt.size())
         .<CompoundNBT>mapToObj(nbt::func_150305_b)
         .forEach(entry -> this.delegate.put(this.readKey.apply(entry), this.readValue.apply(entry)));
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
   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   @Override
   public V get(Object key) {
      return this.delegate.get(key);
   }

   @Override
   public V put(K key, V value) {
      return this.delegate.put(key, value);
   }

   @Override
   public V remove(Object key) {
      return this.delegate.remove(key);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      this.delegate.putAll(m);
   }

   @Override
   public void clear() {
      this.delegate.clear();
   }

   @Override
   public Set<K> keySet() {
      return this.delegate.keySet();
   }

   @Override
   public Collection<V> values() {
      return this.delegate.values();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.delegate.entrySet();
   }

   public static <N extends INBT, T extends INBTSerializable<N>> VMapNBT<UUID, T> ofUUID(Supplier<T> supplier) {
      return (VMapNBT<UUID, T>)(new VMapNBT<>(
         (nbt, uuid) -> nbt.func_74778_a("Key", uuid.toString()),
         (nbt, value) -> nbt.func_218657_a("Value", value.serializeNBT()),
         nbt -> UUID.fromString(nbt.func_74779_i("Key")),
         nbt -> {
            T value = supplier.get();
            value.deserializeNBT(nbt.func_74781_a("Value"));
            return value;
         }
      ));
   }

   public static <N extends INBT, T extends INBTSerializable<N>> VMapNBT<Integer, T> ofInt(Supplier<T> supplier) {
      return ofInt(new HashMap<>(), supplier);
   }

   public static <N extends INBT, T extends INBTSerializable<N>> VMapNBT<Integer, T> ofInt(Map<Integer, T> map, Supplier<T> supplier) {
      return new VMapNBT<>(
         map,
         (nbt, integer) -> nbt.func_74768_a("Key", integer),
         (nbt, value) -> nbt.func_218657_a("Value", value.serializeNBT()),
         nbt -> nbt.func_74762_e("Key"),
         nbt -> {
            T value = supplier.get();
            value.deserializeNBT(nbt.func_74781_a("Value"));
            return value;
         }
      );
   }
}
