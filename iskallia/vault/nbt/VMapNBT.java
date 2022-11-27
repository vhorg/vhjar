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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class VMapNBT<K, V> implements INBTSerializable<ListTag>, Map<K, V> {
   private final Map<K, V> delegate;
   private final BiConsumer<CompoundTag, K> writeKey;
   private final BiConsumer<CompoundTag, V> writeValue;
   private final Function<CompoundTag, K> readKey;
   private final Function<CompoundTag, V> readValue;

   public VMapNBT(
      Map<K, V> map,
      BiConsumer<CompoundTag, K> writeKey,
      BiConsumer<CompoundTag, V> writeValue,
      Function<CompoundTag, K> readKey,
      Function<CompoundTag, V> readValue
   ) {
      this.delegate = map;
      this.writeKey = writeKey;
      this.writeValue = writeValue;
      this.readKey = readKey;
      this.readValue = readValue;
   }

   public VMapNBT(
      BiConsumer<CompoundTag, K> writeKey, BiConsumer<CompoundTag, V> writeValue, Function<CompoundTag, K> readKey, Function<CompoundTag, V> readValue
   ) {
      this(new HashMap<>(), writeKey, writeValue, readKey, readValue);
   }

   public ListTag serializeNBT() {
      ListTag nbt = new ListTag();
      this.delegate.forEach((key, value) -> {
         CompoundTag entry = new CompoundTag();
         this.writeKey.accept(entry, (K)key);
         this.writeValue.accept(entry, (V)value);
         nbt.add(entry);
      });
      return nbt;
   }

   public void deserializeNBT(ListTag nbt) {
      this.delegate.clear();
      IntStream.range(0, nbt.size())
         .<CompoundTag>mapToObj(nbt::getCompound)
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

   public static <N extends Tag, T extends INBTSerializable<N>> VMapNBT<UUID, T> ofUUID(Supplier<T> supplier) {
      return (VMapNBT<UUID, T>)(new VMapNBT<>(
         (nbt, uuid) -> nbt.putString("Key", uuid.toString()),
         (nbt, value) -> nbt.put("Value", value.serializeNBT()),
         nbt -> UUID.fromString(nbt.getString("Key")),
         nbt -> {
            T value = supplier.get();
            value.deserializeNBT(nbt.get("Value"));
            return value;
         }
      ));
   }

   public static <N extends Tag, T extends INBTSerializable<N>> VMapNBT<Integer, T> ofInt(Supplier<T> supplier) {
      return ofInt(new HashMap<>(), supplier);
   }

   public static <N extends Tag, T extends INBTSerializable<N>> VMapNBT<Integer, T> ofInt(Map<Integer, T> map, Supplier<T> supplier) {
      return new VMapNBT<>(
         map, (nbt, integer) -> nbt.putInt("Key", integer), (nbt, value) -> nbt.put("Value", value.serializeNBT()), nbt -> nbt.getInt("Key"), nbt -> {
            T value = supplier.get();
            value.deserializeNBT(nbt.get("Value"));
            return value;
         }
      );
   }
}
