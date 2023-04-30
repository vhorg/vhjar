package iskallia.vault.core.data.adapter.util;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class ForgeRegistryAdapter<V extends IForgeRegistryEntry<V>> implements ISimpleAdapter<V, Tag, JsonElement> {
   private final Supplier<IForgeRegistry<V>> registry;
   private final boolean nullable;

   public ForgeRegistryAdapter(Supplier<IForgeRegistry<V>> registry, boolean nullable) {
      this.registry = registry;
      this.nullable = nullable;
   }

   public IForgeRegistry<V> getRegistry() {
      return this.registry.get();
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ForgeRegistryAdapter<V> asNullable() {
      return new ForgeRegistryAdapter<>(this.registry, true);
   }

   public void writeBits(@Nullable V value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.IDENTIFIER.writeBits(value.getRegistryName(), buffer);
      }
   }

   @Override
   public Optional<V> readBits(BitBuffer buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         ResourceLocation id = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
         return this.getRegistry().containsKey(id) ? Optional.ofNullable((V)this.getRegistry().getValue(id)) : Optional.empty();
      }
   }

   public void writeBytes(@Nullable V value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.IDENTIFIER.writeBytes(value.getRegistryName(), buffer);
      }
   }

   @Override
   public Optional<V> readBytes(ByteBuf buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         ResourceLocation id = Adapters.IDENTIFIER.readBytes(buffer).orElseThrow();
         return this.getRegistry().containsKey(id) ? Optional.ofNullable((V)this.getRegistry().getValue(id)) : Optional.empty();
      }
   }

   public void writeData(@Nullable V value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.IDENTIFIER.writeData(value.getRegistryName(), data);
      }
   }

   @Override
   public Optional<V> readData(DataInput data) throws IOException {
      if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         ResourceLocation id = Adapters.IDENTIFIER.readData(data).orElseThrow();
         return this.getRegistry().containsKey(id) ? Optional.ofNullable((V)this.getRegistry().getValue(id)) : Optional.empty();
      }
   }

   public Optional<Tag> writeNbt(@Nullable V value) {
      return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.getRegistryName());
   }

   @Override
   public Optional<V> readNbt(@Nullable Tag nbt) {
      ResourceLocation id = Adapters.IDENTIFIER.readNbt(nbt).orElse(null);
      return id != null && this.getRegistry().containsKey(id) ? Optional.ofNullable((V)this.getRegistry().getValue(id)) : Optional.empty();
   }

   public Optional<JsonElement> writeJson(@Nullable V value) {
      return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeJson(value.getRegistryName());
   }

   @Override
   public Optional<V> readJson(@Nullable JsonElement json) {
      ResourceLocation id = Adapters.IDENTIFIER.readJson(json).orElse(null);
      return id != null && this.getRegistry().containsKey(id) ? Optional.ofNullable((V)this.getRegistry().getValue(id)) : Optional.empty();
   }
}
