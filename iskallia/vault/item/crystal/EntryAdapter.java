package iskallia.vault.item.crystal;

import com.google.gson.JsonElement;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;

public abstract class EntryAdapter<T, N extends CompoundTag, J extends JsonElement, R extends EntryAdapter<T, N, J, R>> implements ISimpleAdapter<T, N, J> {
   private Map<String, Supplier<? extends T>> idToSupplier = new HashMap<>();
   private Map<Class<? extends T>, String> classToId = new HashMap<>();
   private Map<Class<? extends T>, Integer> classToIndex = new HashMap<>();
   private Map<Integer, Supplier<? extends T>> indexToSupplier = new HashMap<>();

   public R register(String id, Class<? extends T> type, Supplier<? extends T> supplier) {
      this.idToSupplier.put(id, supplier);
      this.classToId.put(type, id);
      this.classToIndex.put(type, this.classToId.size());
      this.indexToSupplier.put(this.indexToSupplier.size(), supplier);
      return (R)this;
   }

   public String getId(T value) {
      return this.classToId.get(value.getClass());
   }

   public int getIndex(T value) {
      return this.classToIndex.get(value.getClass());
   }

   public T getValue(String id) {
      return (T)(this.idToSupplier.containsKey(id) ? this.idToSupplier.get(id).get() : null);
   }

   public T getValue(int index) {
      return (T)(this.indexToSupplier.containsKey(index) ? this.indexToSupplier.get(index).get() : null);
   }
}
