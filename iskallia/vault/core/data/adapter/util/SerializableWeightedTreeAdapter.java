package iskallia.vault.core.data.adapter.util;

import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.util.WeightedTree;
import iskallia.vault.item.crystal.data.adapter.IAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SerializableWeightedTreeAdapter<T, W extends WeightedTree<T>> extends WeightedTreeAdapter<T, W> {
   private final Supplier<W> treeConstructor;
   private Map<String, Supplier<? extends T>> nameToSupplier = new HashMap<>();
   private Map<Class<? extends T>, String> classToName = new HashMap<>();

   public SerializableWeightedTreeAdapter(Supplier<W> treeConstructor) {
      this.treeConstructor = treeConstructor;
   }

   public SerializableWeightedTreeAdapter<T, W> register(String name, Class<? extends T> type, Supplier<? extends T> supplier) {
      this.nameToSupplier.put(name, supplier);
      this.classToName.put(type, name);
      return this;
   }

   @Override
   public W create() {
      return this.treeConstructor.get();
   }

   @Override
   public String getName(T value) {
      return this.classToName.get(value.getClass());
   }

   @Override
   public IAdapter getAdapter(String name) {
      return this.nameToSupplier.containsKey(name) ? new SerializableAdapter<>(this.nameToSupplier.get(name), false) : null;
   }
}
