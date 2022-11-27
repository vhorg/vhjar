package iskallia.vault.core.data.key;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.Field;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.data.sync.handler.SyncHandler;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class FieldKey<T> extends VersionedKey<FieldKey<T>, Field<T>> {
   protected FieldKey(ResourceLocation id) {
      super(id);
   }

   public static <T> FieldKey<T> of(String id, Class<T> type) {
      return new FieldKey<>(VaultMod.id(id));
   }

   public static <T> FieldKey<T> of(ResourceLocation id, Class<T> type) {
      return new FieldKey<>(id);
   }

   public static <T> FieldKey<Supplier<T>> ofSupplier(String id, Class<T> type) {
      return new FieldKey<>(VaultMod.id(id));
   }

   public static <T> FieldKey<Supplier<T>> ofSupplier(ResourceLocation id, Class<T> type) {
      return new FieldKey<>(id);
   }

   public static <E> FieldKey<DataList<?, E>> ofList(String id, Class<E> type) {
      return new FieldKey<>(VaultMod.id(id));
   }

   public static <E> FieldKey<DataList<?, E>> ofList(ResourceLocation id, Class<E> type) {
      return new FieldKey<>(id);
   }

   public static <K, V> FieldKey<DataMap<?, K, V>> ofMap(String id, Class<K> key, Class<V> value) {
      return new FieldKey<>(VaultMod.id(id));
   }

   public static <K, V> FieldKey<DataMap<?, K, V>> ofMap(ResourceLocation id, Class<K> key, Class<V> value) {
      return new FieldKey<>(id);
   }

   public static <T> FieldKey<ResourceKey<T>> ofResourceKey(String id, Class<T> type) {
      return new FieldKey<>(VaultMod.id(id));
   }

   public static <T> FieldKey<ResourceKey<T>> ofResourceKey(ResourceLocation id, Class<T> type) {
      return new FieldKey<>(id);
   }

   public FieldKey<T> with(Version version, Adapter<T> adapter, SyncHandler handler) {
      return this.with(version, adapter, handler, () -> null);
   }

   public FieldKey<T> with(Version version, Adapter<T> adapter, SyncHandler handler, Supplier<T> defaultValue) {
      return (FieldKey<T>)super.with(version, (T)(new Field<T>(adapter, handler, defaultValue)));
   }

   public T validate(Version version, T value, SyncContext context) {
      return this.get(version).validate(value, context);
   }

   public void writeValue(Version version, BitBuffer buffer, SyncContext context, T value) {
      this.get(version).writeValue(buffer, context, value);
   }

   public T readValue(Version version, BitBuffer buffer, SyncContext context) {
      return this.get(version).readValue(buffer, context);
   }

   public boolean canSync(T value, SyncContext context) {
      return this.get(context.getVersion()).canSync(value, context);
   }
}
