package iskallia.vault.core.data.key.registry;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.random.RandomSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public class KeyRegistry<K extends VersionedKey<? extends K, ? extends T>, T> {
   protected List<K> keys = new ArrayList<>();
   protected Map<Version, List<K>> keyCache = new HashMap<>();
   protected Map<Version, Map<ResourceLocation, Integer>> indexCache = new HashMap<>();
   protected Map<ResourceLocation, K> idCache = new HashMap<>();
   protected boolean locked = false;

   public KeyRegistry() {
   }

   public KeyRegistry(K... keys) {
      for (K key : keys) {
         this.register(key);
      }

      this.locked = true;
   }

   public <R extends KeyRegistry<K, T>> R merge(R other) {
      this.keys.forEach(other::register);
      return other;
   }

   public void lock() {
      this.locked = true;
   }

   public boolean contains(K key) {
      return this.idCache.get(key.getId()) != null;
   }

   public K getRandom(RandomSource random, Predicate<K> filter) {
      int size = 1;
      K result = null;

      for (K key : this.keys) {
         if (filter.test(key) && random.nextInt(size++) == 0) {
            result = key;
         }
      }

      return result;
   }

   public K register(K key) {
      if (!this.locked) {
         this.idCache.put(key.getId(), key);
         this.keys.add(key);
         this.keys.sort(Comparator.comparing(VersionedKey::getId));
         return key;
      } else {
         throw new UnsupportedOperationException("Registry is locked");
      }
   }

   public void remove(K key) {
      this.idCache.remove(key.getId());
      this.keys.remove(key);
      this.keyCache.clear();
      this.indexCache.clear();
   }

   public K getKey(String id) {
      return id == null ? null : this.getKey(new ResourceLocation(id));
   }

   public K getKey(ResourceLocation id) {
      return id == null ? null : this.idCache.get(id);
   }

   public List<K> getKeys() {
      return Collections.unmodifiableList(this.keys);
   }

   public int getIndex(ResourceLocation id, Version version) {
      return this.getIndex(this.getKey(id), version);
   }

   public int getIndex(K key, Version version) {
      this.ensureCacheIsPresent(version);
      return this.indexCache.get(version).getOrDefault(key.getId(), -1);
   }

   public K getKey(int index, Version version) {
      this.ensureCacheIsPresent(version);
      return this.keyCache.get(version).get(index);
   }

   public int getSize(Version version) {
      this.ensureCacheIsPresent(version);
      return this.keyCache.get(version).size();
   }

   private void ensureCacheIsPresent(Version version) {
      if (!this.keyCache.containsKey(version)) {
         List<K> keys = new ArrayList<>();
         Map<ResourceLocation, Integer> indices = new HashMap<>();
         int index = 0;

         for (int i = 0; i < this.keys.size(); i++) {
            K key = this.keys.get(i);
            if (key.supports(version)) {
               keys.add(key);
               indices.put(key.getId(), index++);
            }
         }

         this.keyCache.put(version, keys);
         this.indexCache.put(version, indices);
      }
   }
}
