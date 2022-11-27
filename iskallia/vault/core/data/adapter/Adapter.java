package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.ICompound;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;

public abstract class Adapter<T> {
   public abstract T validate(T var1, SyncContext var2);

   public abstract void writeValue(BitBuffer var1, SyncContext var2, T var3);

   public abstract T readValue(BitBuffer var1, SyncContext var2, T var3);

   public static <T> VoidAdapter<T> ofVoid() {
      return new VoidAdapter<>();
   }

   public static <T extends ICompound<?>> CompoundAdapter<T> ofCompound() {
      return new CompoundAdapter<>(null, false);
   }

   public static <T extends ICompound<?>> CompoundAdapter<T> ofCompound(Supplier<T> supplier) {
      return new CompoundAdapter<>(supplier, false);
   }

   public static BooleanAdapter ofBoolean() {
      return new BooleanAdapter();
   }

   public static IntAdapter ofInt() {
      return new IntAdapter();
   }

   public static BoundedIntAdapter ofBoundedInt(int bound) {
      return new BoundedIntAdapter(0, bound - 1);
   }

   public static BoundedIntAdapter ofBoundedInt(int min, int max) {
      return new BoundedIntAdapter(min, max);
   }

   public static SegmentedIntAdapter ofSegmentedInt(int segment) {
      return new SegmentedIntAdapter(segment);
   }

   public static FloatAdapter ofFloat() {
      return new FloatAdapter();
   }

   public static LongAdapter ofLong() {
      return new LongAdapter();
   }

   public static DoubleAdapter ofDouble() {
      return new DoubleAdapter();
   }

   public static UUIDAdapter ofUUID() {
      return new UUIDAdapter(false);
   }

   public static StringAdapter ofString() {
      return new StringAdapter(false);
   }

   public static IdentifierAdapter ofIdentifier() {
      return new IdentifierAdapter(false);
   }

   public static <T> ResourceKeyAdapter<T> ofResourceKey(ResourceKey<Registry<T>> registry) {
      return new ResourceKeyAdapter<>(registry, false);
   }

   public static <E extends Enum<E>> EnumAdapter<E> ofEnum(Class<E> type) {
      return new EnumAdapter<>(type, false);
   }

   public static <T> OrdinalAdapter<T> ofOrdinal(ToIntFunction<T> mapper, T... array) {
      return new OrdinalAdapter<>(mapper, false, array);
   }

   public static BlockPosAdapter ofBlockPos() {
      return new BlockPosAdapter(-29999999, -63, -29999999, 30000000, 320, 30000000, false);
   }

   public static <T extends ICompound<T>, K extends VersionedKey<? extends K, ? extends V>, V> RegistryValueAdapter<T, K, V> ofRegistryValue(
      Supplier<KeyRegistry<K, V>> registry, Function<T, K> serializer, Function<V, T> deserializer
   ) {
      return new RegistryValueAdapter<>(registry, serializer, deserializer);
   }

   public static <K extends VersionedKey<? extends K, ? extends V>, V> OldRegistryKeyAdapter<K, V> ofOldRegistryKey(Supplier<KeyRegistry<K, V>> registry) {
      return new OldRegistryKeyAdapter<>(registry, false);
   }

   public static <K extends VersionedKey<? extends K, ? extends V>, V> RegistryKeyAdapter<K, V> ofRegistryKey(Supplier<KeyRegistry<K, V>> registry) {
      return new RegistryKeyAdapter<>(registry, false);
   }

   public static <T extends Tag> NBTAdapter<T> ofNBT(Class<T> type) {
      return new NBTAdapter<>(type, false);
   }

   public static ItemStackAdapter ofItemStack() {
      return new ItemStackAdapter();
   }

   @FunctionalInterface
   public interface Reader<T> {
      T readValue(BitBuffer var1, SyncContext var2);
   }

   @FunctionalInterface
   public interface Writer<T> {
      void writeValue(BitBuffer var1, SyncContext var2, T var3);
   }
}
