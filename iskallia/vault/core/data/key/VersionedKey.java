package iskallia.vault.core.data.key;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.util.VersionMap;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.resources.ResourceLocation;

public class VersionedKey<K extends VersionedKey<K, T>, T> {
   protected ResourceLocation id;
   protected VersionMap<T> entries = new VersionMap<>();

   protected VersionedKey(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public VersionMap<T> getMap() {
      return this.entries;
   }

   public void setId(ResourceLocation id) {
      this.id = id;
   }

   public T get(Version version) {
      return this.entries.getFor(version).orElseThrow(IllegalStateException::new);
   }

   public boolean supports(Version version) {
      Optional<Version> opt = this.entries.getOldest();
      return opt.isEmpty() ? false : version.isNewerOrEqualTo(opt.get());
   }

   public K with(Version version, T value) {
      this.entries.put(version, value);
      if (value instanceof Keyed<?> keyedValue) {
         keyedValue.setKey(this);
      }

      return (K)this;
   }

   public K withMap(Version version, UnaryOperator<T> value) {
      T previous = this.entries.getFor(version).orElse(null);
      return this.with(version, value.apply(previous));
   }

   public <R extends KeyRegistry<? super K, ? super T>> K register(R registry) {
      registry.register((K)this);
      return (K)this;
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public String toString() {
      return this.id.toString();
   }
}
