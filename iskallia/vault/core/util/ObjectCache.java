package iskallia.vault.core.util;

import iskallia.vault.VaultMod;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import net.minecraft.util.Mth;

public class ObjectCache<K, V> {
   private final long[] keys;
   private final V[] values;
   private final int mask;
   private final ToLongFunction<K> hash;

   public ObjectCache(int capacity, ToLongFunction<K> hash) {
      int newCapacity = capacity;
      if (capacity < 0) {
         newCapacity = 0;
         VaultMod.LOGGER.warn("cache capacity must be >= 0, setting to 0");
      } else if (!MathUtils.isPowerOf2(capacity) && capacity != 0) {
         newCapacity = Mth.smallestEncompassingPowerOfTwo(capacity);
         VaultMod.LOGGER.warn("cache capacity must be a power of 2, setting to " + newCapacity + " instead of " + capacity);
      }

      this.keys = new long[newCapacity];
      Arrays.fill(this.keys, -1L);
      this.values = (V[])(new Object[newCapacity]);
      this.mask = (int)MathUtils.getMask(Long.numberOfTrailingZeros(newCapacity));
      this.hash = hash;
   }

   public boolean has(K object) {
      if (this.values.length == 0) {
         return false;
      } else {
         long key = this.hash.applyAsLong(object);
         int id = this.murmur64(key) & this.mask;
         return this.keys[id] == key;
      }
   }

   public V get(K object) {
      if (this.values.length == 0) {
         return null;
      } else {
         long key = this.hash.applyAsLong(object);
         int id = this.murmur64(key) & this.mask;
         return this.values[id];
      }
   }

   public void set(K object, V value) {
      if (this.values.length != 0) {
         long key = this.hash.applyAsLong(object);
         int id = this.murmur64(key) & this.mask;
         this.keys[id] = key;
         this.values[id] = value;
      }
   }

   public V getOrCreate(K object, Function<K, V> sampler) {
      if (this.values.length == 0) {
         return sampler.apply(object);
      } else {
         long key = this.hash.applyAsLong(object);
         int id = this.murmur64(key) & this.mask;
         if (this.keys[id] == key) {
            return this.values[id];
         } else {
            V value = sampler.apply(object);
            this.keys[id] = key;
            this.values[id] = value;
            return value;
         }
      }
   }

   public void remove(K object) {
      if (this.values.length != 0) {
         long key = this.hash.applyAsLong(object);
         int id = this.murmur64(key) & this.mask;
         if (this.keys[id] == key) {
            this.values[id] = null;
         }
      }
   }

   protected int murmur64(long value) {
      value ^= value >>> 33;
      value *= -49064778989728563L;
      value ^= value >>> 33;
      value *= -4265267296055464877L;
      value ^= value >>> 33;
      return (int)value;
   }
}
