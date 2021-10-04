package iskallia.vault.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class EnumCodec<T extends Enum<T>> implements Codec<T> {
   private final Class<T> enumClass;

   private EnumCodec(Class<T> enumClass) {
      this.enumClass = enumClass;
   }

   public static <T extends Enum<T>> EnumCodec<T> of(Class<T> clazz) {
      return new EnumCodec<>(clazz);
   }

   public <V> DataResult<Pair<T, V>> decode(DynamicOps<V> ops, V input) {
      return ops.getNumberValue(input).map(Number::intValue).map(i -> MiscUtils.getEnumEntry(this.enumClass, i)).map(r -> Pair.of(r, ops.empty()));
   }

   public <V> DataResult<V> encode(T input, DynamicOps<V> ops, V prefix) {
      return ops.mergeToPrimitive(prefix, ops.createInt(input.ordinal()));
   }
}
